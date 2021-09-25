package ch.weetech.cluster;

import ch.weetech.action.AbstractMultiINodeActionBuilder;
import ch.weetech.action.GenericResultAbstractAction;

public class NodesInfo extends GenericResultAbstractAction {
	
    protected NodesInfo(Builder builder) {
        super(builder);
    }
    
    @Override
    public String getRestMethodName() {
        return "GET";
    }
	
	
    public static class Builder extends AbstractMultiINodeActionBuilder<NodesInfo, Builder> {

        public Builder withSettings() {
            return addCleanApiParameter("settings");
        }

        public Builder withOs() {
            return addCleanApiParameter("os");
        }

        public Builder withProcess() {
            return addCleanApiParameter("process");
        }

        public Builder withJvm() {
            return addCleanApiParameter("jvm");
        }

        public Builder withThreadPool() {
            return addCleanApiParameter("thread_pool");
        }

        public Builder withNetwork() {
            return addCleanApiParameter("network");
        }

        public Builder withTransport() {
            return addCleanApiParameter("transport");
        }

        public Builder withHttp() {
            return addCleanApiParameter("http");
        }

        public Builder withPlugins() {
            return addCleanApiParameter("plugins");
        }

        @Override
        public NodesInfo build() {
            return new NodesInfo(this);
        }

    }

}
