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
package ch.weetech.core;

import ch.weetech.action.SingleResultAbstractDocumentTargetedAction;

public class Get extends SingleResultAbstractDocumentTargetedAction {

    protected Get(Builder builder) {
        super(builder);
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    public String getPathToResult() {
        return "_source";
    }

    public static class Builder extends SingleResultAbstractDocumentTargetedAction.Builder<Get, Builder> {

        /**
         * Index and ID parameters are mandatory but type is optional (_all will be used for type if left blank).
         * <br/><br/>
         * The get API allows for _type to be optional. Set it to _all in order to fetch the
         * first document matching the id across all types.
         */
        public Builder(String index, String id) {
            this.index(index);
            this.id(id);
            this.type("_all");
        }

        public Builder(String path) {
            this.path(path);
        }

        public Get build() {
            return new Get(this);
        }
    }

}
