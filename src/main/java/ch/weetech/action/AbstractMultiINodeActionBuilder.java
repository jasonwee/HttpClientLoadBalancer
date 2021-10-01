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

import java.util.Collection;
import java.util.LinkedList;

import com.google.common.base.Joiner;

public abstract class AbstractMultiINodeActionBuilder<T extends Action, K> extends AbstractAction.Builder<T, K> {
    protected Collection<String> nodes = new LinkedList<String>();

    /**
     * Most cluster level APIs allow to specify which nodes to execute on (for example, getting the node stats for a node).
     * Nodes can be identified in the APIs either using their internal node id, the node name, address, custom attributes,
     * or just the _local node receiving the request. For example, here are some sample values for node:
     * <p/>
     * <pre>
     *    # Local   ->  _local
     *
     *    # Address ->  10.0.0.3,10.0.0.4
     *              ->  10.0.0.*
     *
     *    # Names   ->  node_name_goes_here
     *              ->  node_name_goes_*
     *
     *    # Attributes (set something like node.rack: 2 in the config)
     *              ->  rack:2
     *                  ->  ra*:2
     *              ->  ra*:2*
     * </pre>
     */
    public K addNode(String node) {
        if (node != null && !node.isEmpty()) {
            nodes.add(node);
        }
        return (K) this;
    }

    /**
     * Most cluster level APIs allow to specify which nodes to execute on (for example, getting the node stats for a node).
     * Nodes can be identified in the APIs either using their internal node id, the node name, address, custom attributes,
     * or just the _local node receiving the request. For example, here are some sample values for node:
     * <p/>
     * <pre>
     *    # Local   ->  _local
     *
     *    # Address ->  10.0.0.3,10.0.0.4
     *              ->  10.0.0.*
     *
     *    # Names   ->  node_name_goes_here
     *              ->  node_name_goes_*
     *
     *    # Attributes (set something like node.rack: 2 in the config)
     *              ->  rack:2
     *                  ->  ra*:2
     *              ->  ra*:2*
     * </pre>
     */
    public K addNode(Collection<? extends String> nodes) {
        this.nodes.addAll(nodes);
        return (K) this;
    }

    public String getJoinedNodes() {
        if (!nodes.isEmpty()) {
            return Joiner.on(',').join(nodes);
        } else {
            return "_all";
        }
    }

    abstract public T build();
}
