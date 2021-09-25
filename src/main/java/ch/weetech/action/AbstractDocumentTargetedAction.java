package ch.weetech.action;

import ch.weetech.client.JwResult;
import ch.weetech.client.config.ElasticsearchVersion;

public abstract class AbstractDocumentTargetedAction<T extends JwResult> extends AbstractAction<T> implements DocumentTargetedAction<T> {

    protected String id;

    public AbstractDocumentTargetedAction(Builder builder) {
        super(builder);
        indexName = builder.index;
        typeName = builder.type;
        id = builder.id;
    }

    @Override
    public String getIndex() {
        return indexName;
    }

    @Override
    public String getType() {
        return typeName;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
    	System.out.println("AbstractDocumentTargetedAction builduri");
        StringBuilder sb = new StringBuilder(super.buildURI(elasticsearchVersion));

        //if (StringUtils.isNotBlank(id)) {
        if (id != null && !id.isEmpty()) {
            sb.append("/").append(id);
        }
        
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    protected abstract static class Builder<T extends AbstractDocumentTargetedAction, K> extends AbstractAction.Builder<T, K> {
        private String index;
        private String type;
        private String id;

        public K index(String index) {
            this.index = index;
            return (K) this;
        }

        public K type(String type) {
            this.type = type;
            return (K) this;
        }

        public K id(String id) {
            this.id = id;
            return (K) this;
        }

    }

}
