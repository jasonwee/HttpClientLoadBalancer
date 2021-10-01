package ch.weetech.client.http;

import java.io.IOException;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ch.weetech.action.Action;
import ch.weetech.client.AbstractJwClient;
import ch.weetech.client.JwResult;
import ch.weetech.client.config.ElasticsearchVersion;
import ch.weetech.client.config.exception.CouldNotConnectException;
import ch.weetech.client.http.apache.HttpDeleteWithEntity;
import ch.weetech.client.http.apache.HttpGetWithEntity;

public class JwHttpClient extends AbstractJwClient {

    private final static Logger log = LoggerFactory.getLogger(JwHttpClient.class);

    protected ContentType requestContentType = ContentType.APPLICATION_JSON.withCharset("utf-8");

    private CloseableHttpClient httpClient;
    private CloseableHttpAsyncClient asyncClient;

    private HttpClientContext httpClientContextTemplate;

    private ElasticsearchVersion elasticsearchVersion = ElasticsearchVersion.UNKNOWN;


    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setAsyncClient(CloseableHttpAsyncClient asyncClient) {
        this.asyncClient = asyncClient;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public void setHttpClientContextTemplate(HttpClientContext httpClientContext) {
        this.httpClientContextTemplate = httpClientContext;
    }

    @Override
    public <T extends JwResult> T execute(Action<T> clientRequest) throws IOException {
        return execute(clientRequest, null);
    }

    public <T extends JwResult> T execute(Action<T> clientRequest, RequestConfig requestConfig) throws IOException {
        HttpUriRequest request = prepareRequest(clientRequest, requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = executeRequest(request);
            return deserializeResponse(response, request, clientRequest);
        } catch (HttpHostConnectException ex) {
            throw new CouldNotConnectException(ex.getHost().toURI(), ex);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ex) {
                    log.error("Exception occurred while closing response stream.", ex);
                }
            }
        }
    }

    protected <T extends JwResult> HttpUriRequest prepareRequest(final Action<T> clientRequest, final RequestConfig requestConfig) {
        String elasticSearchRestUrl = getRequestURL(getNextServer(), clientRequest.getURI(elasticsearchVersion));
        HttpUriRequest request = constructHttpMethod(clientRequest.getRestMethodName(), elasticSearchRestUrl, clientRequest.getData(gson), requestConfig);

        log.debug("Request method={} url={}", clientRequest.getRestMethodName(), elasticSearchRestUrl);

        // add headers added to action
        for (Entry<String, Object> header : clientRequest.getHeaders().entrySet()) {
            System.out.println("add header=" + header.getKey());
            request.addHeader(header.getKey(), header.getValue().toString());
        }

        return request;
    }

    protected CloseableHttpResponse executeRequest(HttpUriRequest request) throws IOException {
        if (httpClientContextTemplate != null) {
            return httpClient.execute(request, createContextInstance());
        }

        return httpClient.execute(request);
    }

    protected HttpClientContext createContextInstance() {
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(httpClientContextTemplate.getCredentialsProvider());
        context.setAuthCache(httpClientContextTemplate.getAuthCache());

        return context;
    }

    protected HttpUriRequest constructHttpMethod(String methodName, String url, String payload, RequestConfig requestConfig) {
        HttpUriRequest httpUriRequest = null;

        if (methodName.equalsIgnoreCase("POST")) {
            httpUriRequest = new HttpPost(url);
            log.debug("POST method created based on client request");
        } else if (methodName.equalsIgnoreCase("PUT")) {
            httpUriRequest = new HttpPut(url);
            log.debug("PUT method created based on client request");
        } else if (methodName.equalsIgnoreCase("DELETE")) {
            httpUriRequest = new HttpDeleteWithEntity(url);
            log.debug("DELETE method created based on client request");
        } else if (methodName.equalsIgnoreCase("GET")) {
            httpUriRequest = new HttpGetWithEntity(url);
            log.debug("GET method created based on client request");
        } else if (methodName.equalsIgnoreCase("HEAD")) {
            httpUriRequest = new HttpHead(url);
            log.debug("HEAD method created based on client request");
        }

        if (httpUriRequest instanceof HttpRequestBase && requestConfig != null) {
            ((HttpRequestBase) httpUriRequest).setConfig(requestConfig);
        }

        if (httpUriRequest != null && httpUriRequest instanceof HttpEntityEnclosingRequest && payload != null) {
            EntityBuilder entityBuilder = EntityBuilder.create()
                    .setText(payload)
                    .setContentType(requestContentType);

            if (isRequestCompressionEnabled()) {
                entityBuilder.gzipCompress();
            }

            ((HttpEntityEnclosingRequest) httpUriRequest).setEntity(entityBuilder.build());
        }

        return httpUriRequest;
    }

    private <T extends JwResult> T deserializeResponse(HttpResponse response, final HttpRequest httpRequest, Action<T> clientRequest) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        try {
            return clientRequest.createNewElasticSearchResult(
                    response.getEntity() == null ? null : EntityUtils.toString(response.getEntity()),
                    statusLine.getStatusCode(),
                    statusLine.getReasonPhrase(),
                    gson
            );
        } catch (com.google.gson.JsonSyntaxException e) {
            for (Header header : response.getHeaders("Content-Type")) {
                final String mimeType = header.getValue();
                if (!mimeType.startsWith("application/json")) {
                    // probably a proxy that responded in text/html
                    final String message = "Request " + httpRequest.toString() + " yielded " + mimeType
                            + ", should be json: " + statusLine.toString();
                    throw new IOException(message, e);
                }
            }
            throw e;
        }
    }


}
