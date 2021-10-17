/*
 * Copyright [2021] [Jason Wee]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.weetech.client.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.GzipCompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import ch.weetech.client.JwClientFactory;
import ch.weetech.client.JwResult;
import ch.weetech.client.config.HttpClientConfig;
import ch.weetech.core.Put;


public class JwHttpClientTest {

    JwHttpClient client;

    @Before
    public void init() {
        client = new JwHttpClient();
    }

    @After
    public void cleanup() {
        client = null;
    }

    @Test
    public void contructCompressedHttpMethod() throws Exception {
        client.setRequestCompressionEnabled(true);

        HttpUriRequest request = client.constructHttpMethod("PUT", "jw/put", "data", null);
        assertNotNull(request);
        assertEquals(request.getURI().getPath(), "jw/put");
        assertTrue(request instanceof HttpPut);
        assertTrue(((HttpPut)request).getEntity() instanceof GzipCompressingEntity);
    }

    @Test
    public void testUnit() throws Exception {

        String response = "{\"status\": \"ok\"}";

        CloseableHttpClient closableHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse httpResponseMock = mock(CloseableHttpResponse.class);
        HttpEntity entity = mock(HttpEntity.class);
        entity = EntityBuilder.create().setText(response).build();

        StatusLine statusLine = mock(StatusLine.class);
        Mockito.when(closableHttpClient.execute(any(HttpUriRequest.class))).thenReturn(httpResponseMock);
        Mockito.when(httpResponseMock.getStatusLine()).thenReturn(statusLine);
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Mockito.when(httpResponseMock.getEntity()).thenReturn(entity);

        JwClientFactory factory = new JwClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder("http://localhost").build());
        JwHttpClient mockedClient = (JwHttpClient)factory.getObject();
        mockedClient.setHttpClient(closableHttpClient);

        String payload = "{\"foo\": \"bar\"}";

        Put put = new Put.Builder("api/test", payload).build();
        JwResult result = mockedClient.execute(put);

        verify(closableHttpClient, Mockito.times(1)).execute(any(HttpUriRequest.class));
        assertEquals(200, result.getResponseCode());
        assertTrue("\"ok\"".equals(result.getJsonObject().get("status").toString()));
    }


}
