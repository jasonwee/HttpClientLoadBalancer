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

import ch.weetech.client.JwResult;
import ch.weetech.client.config.ElasticsearchVersion;

public abstract class AbstractDocumentTargetedAction<T extends JwResult> extends AbstractAction<T> implements DocumentTargetedAction<T> {

    protected String id;
    protected String path;

    public AbstractDocumentTargetedAction(Builder builder) {
        super(builder);
        indexName = builder.index;
        typeName = builder.type;
        id = builder.id;
        path = builder.path;
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
        StringBuilder sb = new StringBuilder(super.buildURI(elasticsearchVersion));

        //if (StringUtils.isNotBlank(id)) {
        if (id != null && !id.isEmpty()) {
            sb.append("/").append(id);
        }
        if (path != null) {
            sb.append("/").append(path);
        }

        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    protected abstract static class Builder<T extends AbstractDocumentTargetedAction, K> extends AbstractAction.Builder<T, K> {
        private String index;
        private String type;
        private String id;
        private String path;

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

        public K path(String path) {
            this.path = path;
            return (K) this;
        }

    }

}
