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
package ch.weetech.client.config.idle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractScheduledService;

import ch.weetech.client.config.ClientConfig;

public class IdleConnectionReaper extends AbstractScheduledService {

    final static Logger logger = LoggerFactory.getLogger(IdleConnectionReaper.class);

    private final ReapableConnectionManager reapableConnectionManager;
    private final ClientConfig clientConfig;

    public IdleConnectionReaper(ClientConfig clientConfig, ReapableConnectionManager reapableConnectionManager) {
        this.reapableConnectionManager = reapableConnectionManager;
        this.clientConfig = clientConfig;
    }

    @Override
    protected void runOneIteration() throws Exception {
        logger.debug("closing idle connections...");
        reapableConnectionManager.closeIdleConnections(clientConfig.getMaxConnectionIdleTime(),
                                                       clientConfig.getMaxConnectionIdleTimeDurationTimeUnit());

    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0l,
                clientConfig.getMaxConnectionIdleTime(),
                clientConfig.getMaxConnectionIdleTimeDurationTimeUnit());
    }

}
