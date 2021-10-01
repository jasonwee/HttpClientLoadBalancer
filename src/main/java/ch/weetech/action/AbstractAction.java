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
package ch.weetech.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import ch.weetech.client.JwResult;
import ch.weetech.client.config.ElasticsearchVersion;
import ch.weetech.params.Parameters;

public abstract class AbstractAction<T extends JwResult> implements Action<T> {

    public static String CHARSET = "utf-8";

    protected final static Logger log = LoggerFactory.getLogger(AbstractAction.class);

    protected String indexName;
    protected String typeName;
    protected String nodes;
    protected Object payload;

    private final ConcurrentMap<String, Object> headerMap = new ConcurrentHashMap<String, Object>();
    private final Multimap<String, Object> parameterMap = LinkedHashMultimap.create();
    private final Set<String> cleanApiParameters = new LinkedHashSet<String>();

    @Override
    public abstract String getRestMethodName();

    public AbstractAction() {
    }

    @SuppressWarnings("unchecked")
    public AbstractAction(Builder builder) {
        parameterMap.putAll(builder.parameters);
        headerMap.putAll(builder.headers);
        cleanApiParameters.addAll(builder.cleanApiParameters);

        if (builder instanceof AbstractMultiIndexActionBuilder) {
            indexName = ((AbstractMultiIndexActionBuilder) builder).getJoinedIndices();
            if (builder instanceof AbstractMultiTypeActionBuilder) {
                indexName = ((AbstractMultiTypeActionBuilder) builder).getJoinedIndices();
                typeName = ((AbstractMultiTypeActionBuilder) builder).getJoinedTypes();
            }
        } else if (builder instanceof AbstractMultiINodeActionBuilder) {
            nodes = ((AbstractMultiINodeActionBuilder) builder).getJoinedNodes();
        }
    }

    @Override
    public String getData(Gson gson) {
        if (payload == null) {
            return null;
        } else if (payload instanceof String) {
            return (String) payload;
        } else {
            return gson.toJson(payload);
        }
    }

    protected T createNewElasticSearchResult(T result, String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        JsonObject jsonMap = parseResponseBody(responseBody);
        result.setResponseCode(statusCode);
        result.setJsonString(responseBody);
        result.setJsonObject(jsonMap);
        result.setPathToResult(getPathToResult());

        if (isHttpSuccessful(statusCode)) {
            result.setSucceeded(true);
            log.debug("Request and operation succeeded");
        } else {
            result.setSucceeded(false);
            // provide the generic HTTP status code error, if one hasn't already come in via the JSON response...
            // eg.
            //  IndicesExist will return 404 (with no content at all) for a missing index, but:
            //  Update will return 404 (with an error message for DocumentMissingException)
            if (result.getErrorMessage() == null) {
                result.setErrorMessage(statusCode + " " + (reasonPhrase == null ? "null" : reasonPhrase));
            }
            log.debug("Response is failed. errorMessage is " + result.getErrorMessage());
        }
        return result;
    }

    @Override
    public String getPathToResult() {
        return null;
    }

    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        StringBuilder sb = new StringBuilder();

        try {
            //if (StringUtils.isNotBlank(indexName)) {
            if (indexName != null && !indexName.isEmpty()) {

                sb.append(URLEncoder.encode(indexName, CHARSET));

                /*
                //String commandExtension = getURLCommandExtension(elasticsearchVersion);
                String commandExtension = null;

                //if (StringUtils.isNotBlank(commandExtension)) {
                if (commandExtension != null && !commandExtension.isEmpty()) {
                    sb.append("/").append(URLEncoder.encode(commandExtension, CHARSET));
                }
                */

                //if (StringUtils.isNotBlank(typeName)) {
                if (typeName != null && !typeName.isEmpty()) {
                    sb.append("/").append(URLEncoder.encode(typeName, CHARSET));
                }
            }
        } catch (UnsupportedEncodingException e) {
            // unless CHARSET is overridden with a wrong value in a subclass,
            // this exception won't be thrown.
            log.error("Error occurred while adding index/type to uri", e);
        }

        return sb.toString();
    }

    @Override
    public Map<String, Object> getHeaders() {
        return headerMap;
    }

    @Override
    public String getURI(ElasticsearchVersion elasticsearchVersion) {
        String finalUri = buildURI(elasticsearchVersion);
        if (!parameterMap.isEmpty() || !cleanApiParameters.isEmpty()) {
            try {
                finalUri += buildQueryString();
            } catch (UnsupportedEncodingException e) {
                // unless CHARSET is overridden with a wrong value in a subclass,
                // this exception won't be thrown.
                log.error("Error occurred while adding parameters to uri.", e);
            }
        }
        return finalUri;
    }

    protected String buildQueryString() throws UnsupportedEncodingException {
        StringBuilder queryString = new StringBuilder();

        if (!cleanApiParameters.isEmpty()) {
            queryString.append("/").append(Joiner.on(',').join(cleanApiParameters));
            log.info("queryString {}", queryString.toString());
        }

        queryString.append("?");
        for (Map.Entry<String, Object> entry : parameterMap.entries()) {
            queryString.append(URLEncoder.encode(entry.getKey(), CHARSET))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue().toString(), CHARSET))
                    .append("&");
        }
        // if there are any params  ->  deletes the final ampersand
        // if no params             ->  deletes the question mark
        queryString.deleteCharAt(queryString.length() - 1);

        return queryString.toString();
    }


    protected boolean isHttpSuccessful(int httpCode) {
        return (httpCode / 100) == 2;
    }

    protected JsonObject parseResponseBody(String responseBody) {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return new JsonObject();
        }

        JsonElement parsed = new JsonParser().parse(responseBody);
        if (parsed.isJsonObject()) {
            return parsed.getAsJsonObject();
        } else {
            throw new JsonSyntaxException("Response did not contain a JSON Object");
        }
    }

    @SuppressWarnings("unchecked")
    protected static abstract class Builder<T extends Action, K> {
        protected Multimap<String, Object> parameters = LinkedHashMultimap.<String, Object>create();
        protected Map<String, Object> headers = new LinkedHashMap<String, Object>();
        protected Set<String> cleanApiParameters = new LinkedHashSet<String>();

        public K toggleApiParameter(String key, boolean enable) {
            if (enable) {
                addCleanApiParameter(key);
            } else {
                removeCleanApiParameter(key);
            }

            return (K) this;
        }

        public K removeCleanApiParameter(String key) {
            cleanApiParameters.remove(key);
            return (K) this;
        }

        public K addCleanApiParameter(String key) {
            cleanApiParameters.add(key);
            return (K) this;
        }

        public K setParameter(String key, Object value) {
            parameters.put(key, value);
            return (K) this;
        }

        @Deprecated
        public K setParameter(Map<String, Object> parameters) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                this.parameters.put(entry.getKey(), entry.getValue());
            }
            return (K) this;
        }

        public K setHeader(String key, Object value) {
            headers.put(key, value);
            return (K) this;
        }

        public K setHeader(Map<String, Object> headers) {
            this.headers.putAll(headers);
            return (K) this;
        }

        public K refresh(boolean refresh) {
            return setParameter(Parameters.REFRESH, refresh);
        }

        /**
         * All REST APIs accept the case parameter.
         * When set to camelCase, all field names in the result will be returned
         * in camel casing, otherwise, underscore casing will be used. Note,
         * this does not apply to the source document indexed.
         */
        public K resultCasing(String caseParam) {
            setParameter(Parameters.RESULT_CASING, caseParam);
            return (K) this;
        }

        abstract public T build();
    }

}
