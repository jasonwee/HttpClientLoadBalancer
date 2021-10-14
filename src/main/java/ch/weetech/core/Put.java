package ch.weetech.core;

import ch.weetech.action.SingleResultAbstractDocumentTargetedAction;

public class Put extends SingleResultAbstractDocumentTargetedAction {

    protected Put(Builder builder) {
        super(builder);
        this.payload = builder.query;
    }

    @Override
    public String getRestMethodName() {
        return "PUT";
    }

    public static class Builder extends SingleResultAbstractDocumentTargetedAction.Builder<Put, Builder> {
        private final Object query;

        public Builder(String index, String type, String id, Object query) {
            this.index(index);
            this.type(type);
            this.id(id);
            this.query = query;
        }

        public Builder(String path, Object query) {
            this.path(path);
            this.query = query;
        }

        public Put build() {
            return new Put(this);
        }

    }

}
