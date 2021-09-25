package ch.weetech.action;

import com.google.gson.Gson;

import ch.weetech.client.JwResult;

public abstract class GenericResultAbstractAction extends AbstractAction<JwResult> {
    public GenericResultAbstractAction() {
    }

    public GenericResultAbstractAction(Builder builder) {
        super(builder);
    }

    @Override
    public JwResult createNewElasticSearchResult(String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        return createNewElasticSearchResult(new JwResult(gson), responseBody, statusCode, reasonPhrase, gson);
    }

}
