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
