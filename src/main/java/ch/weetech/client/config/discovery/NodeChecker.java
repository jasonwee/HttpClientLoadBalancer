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
package ch.weetech.client.config.discovery;

import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ch.weetech.client.JwClient;
import ch.weetech.client.JwResult;
import ch.weetech.client.config.ClientConfig;
import ch.weetech.client.config.exception.CouldNotConnectException;
import ch.weetech.cluster.NodesInfo;

public class NodeChecker extends AbstractScheduledService {

    private final static Logger log = LoggerFactory.getLogger(NodeChecker.class);
    private final static String PUBLISH_ADDRESS_KEY = "http_address";
    private final static String PUBLISH_ADDRESS_KEY_V5 = "publish_address"; // The one that under "http" node
    private final static Pattern INETSOCKETADDRESS_PATTERN = Pattern.compile("(?:inet\\[)?(?:(?:[^:]+)?\\/)?([^:]+):(\\d+)\\]?");

    private final NodesInfo action;

    protected JwClient client;
    protected Scheduler scheduler;
    protected String defaultScheme;
    protected Set<String> bootstrapServerList;
    protected Set<String> discoveredServerList;

    public NodeChecker(JwClient jwClient, ClientConfig clientConfig) {
        action = new NodesInfo.Builder()
                .withHttp()
                .addNode(clientConfig.getDiscoveryFilter())
                .build();
        this.client = jwClient;
        this.defaultScheme = clientConfig.getDefaultSchemeForDiscoveredNodes();
        this.scheduler = Scheduler.newFixedDelaySchedule(
                0l,
                clientConfig.getDiscoveryFrequency(),
                clientConfig.getDiscoveryFrequencyTimeUnit()
        );
        this.bootstrapServerList = ImmutableSet.copyOf(clientConfig.getServerList());
        this.discoveredServerList = new LinkedHashSet<String>();
    }

    @Override
    protected void runOneIteration() throws Exception {
        JwResult result;
        try {
            result = client.execute(action);
        } catch (CouldNotConnectException cnce) {
            // Can't connect to this node, remove it from the list
            log.error("Connect exception executing NodesInfo!", cnce);
            removeNodeAndUpdateServers(cnce.getHost());
            return;
            // do not elevate the exception since that will stop the scheduled calls.
            // throw new RuntimeException("Error executing NodesInfo!", e);
        } catch (Exception e) {
            log.error("Error executing NodesInfo!", e);
            client.setServers(bootstrapServerList);
            return;
            // do not elevate the exception since that will stop the scheduled calls.
            // throw new RuntimeException("Error executing NodesInfo!", e);
        }

        if (result.isSucceeded()) {
            LinkedHashSet<String> httpHosts = new LinkedHashSet<String>();

            JsonObject jsonMap = result.getJsonObject();
            JsonObject nodes = (JsonObject) jsonMap.get("nodes");
            if (nodes != null) {
                for (Entry<String, JsonElement> entry : nodes.entrySet()) {

                    JsonObject host = entry.getValue().getAsJsonObject();
                    JsonElement addressElement = null;
                    if (host.has("version")) {
                        int majorVersion = Integer.parseInt(Splitter.on('.').splitToList(host.get("version").getAsString()).get(0));

                        if (majorVersion >= 5) {
                            JsonObject http = host.getAsJsonObject("http");
                            if (http != null && http.has(PUBLISH_ADDRESS_KEY_V5))
                                addressElement = http.get(PUBLISH_ADDRESS_KEY_V5);
                        }
                    }

                    if (addressElement == null) {
                        // get as a JsonElement first as some nodes in the cluster may not have an http_address
                        if (host.has(PUBLISH_ADDRESS_KEY)) addressElement = host.get(PUBLISH_ADDRESS_KEY);
                    }

                    if (addressElement != null && !addressElement.isJsonNull()) {
                        String httpAddress = getHttpAddress(addressElement.getAsString());
                        if(httpAddress != null) httpHosts.add(httpAddress);
                    }
              }
            }
            if (log.isDebugEnabled()) {
                log.debug("Discovered {} HTTP hosts: {}", httpHosts.size(), Joiner.on(',').join(httpHosts));
            }
            discoveredServerList = httpHosts;
            client.setServers(discoveredServerList);
        } else {
            log.warn("NodesInfo request resulted in error: {}", result.getErrorMessage());
            client.setServers(bootstrapServerList);
        }

    }

    protected void removeNodeAndUpdateServers(final String hostToRemove) {
        log.warn("Removing host {}", hostToRemove);
        discoveredServerList.remove(hostToRemove);
        if (log.isInfoEnabled()) {
            log.info("Discovered server pool is now: {}", Joiner.on(',').join(discoveredServerList));
        }
        if (!discoveredServerList.isEmpty()) {
          client.setServers(discoveredServerList);
        } else {
          client.setServers(bootstrapServerList);
        }
    }

    @Override
    protected Scheduler scheduler() {
        return scheduler;
    }

    /**
     * Converts the Elasticsearch reported publish address in the format "inet[<hostname>:<port>]" or
     * "inet[<hostname>/<hostaddress>:<port>]" to a normalized http address in the form "http://host:port".
     */
    protected String getHttpAddress(String httpAddress) {
        Matcher resolvedMatcher = INETSOCKETADDRESS_PATTERN.matcher(httpAddress);
        if (resolvedMatcher.matches()) {
            return defaultScheme + resolvedMatcher.group(1) + ":" + resolvedMatcher.group(2);
        }

        return null;
    }

}
