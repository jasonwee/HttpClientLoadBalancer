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

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.base.Joiner;

public abstract class AbstractMultiIndexActionBuilder<T extends Action, K> extends AbstractAction.Builder<T, K> {

    protected Set<String> indexNames = new LinkedHashSet<String>();

    public String getJoinedIndices() {
        if (indexNames.size() > 0) {
            return Joiner.on(',').join(indexNames);
        } else {
            return "_all";
        }
    }

}
