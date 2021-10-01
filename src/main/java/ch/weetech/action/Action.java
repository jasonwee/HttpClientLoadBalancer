package ch.weetech.action;

import java.util.Map;

import com.google.gson.Gson;

import ch.weetech.client.JwResult;
import ch.weetech.client.config.ElasticsearchVersion;

public interface Action<T extends JwResult> {

    String getRestMethodName();

    String getURI(ElasticsearchVersion elasticsearchVersion);

    String getData(Gson gson);

    String getPathToResult();

    Map<String, Object> getHeaders();

    T createNewElasticSearchResult(String responseBody, int statusCode, String reasonPhrase, Gson gson);

}
