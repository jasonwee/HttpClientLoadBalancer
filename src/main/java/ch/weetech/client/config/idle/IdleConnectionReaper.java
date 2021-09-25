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