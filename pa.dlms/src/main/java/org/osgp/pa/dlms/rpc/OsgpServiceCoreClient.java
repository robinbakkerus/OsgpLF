package org.osgp.pa.dlms.rpc;

import org.osgp.pa.dlms.dlms.stats.DlmsStatistics;
import org.osgp.util.Server;
import org.osgp.util.rpc.AbstractOsgpServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.RequestResponseMsg;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * This is gRpc client for Dlms protocol adapter
 * 
 * @author robin
 *
 */
public class OsgpServiceCoreClient extends AbstractOsgpServiceClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(OsgpServiceCoreClient.class.getName());

	private static Config config = ConfigFactory.load("dlms");
	private static final int MAX_MSG_COUNT = config.getInt("grpc.response-stream.max");
	
	private static int totalSend = 0;
	private int msgCount = 0;
	private boolean alreadyClosed = false;

	public OsgpServiceCoreClient(final Server server) {
		super(server);
	}
	
	public void addResponse(final RequestResponseMsg reqRespMsg) {
		DlmsStatistics.incResponsesOut();
		addNext(reqRespMsg);
	}

	void triggerService() {
		addNext(null);
	}

	/*
	 * todo deze moet op een of andere manier gesynced worden met triggerService, zodat exact gelijk aangeroepen kunnen worden.
	 * dit werkt bijna maar nog niet helemaal en is clumpsy
	 */
	private synchronized void addNext(final RequestResponseMsg reqRespMsg) {
		showTotalSend(); 

		if (this.isAlreadyClosed()) {
			makeNewOsgpServiceClient(reqRespMsg);
		} else {
			if (reqRespMsg == null) {
				closeStream();
			} else {
				this.msgCount++;
				addNextRequest(reqRespMsg);
				if (this.msgCount >= MAX_MSG_COUNT) {
					closeStream();					
				}
			}
		}
	}

	private void showTotalSend() {
		totalSend++;
		if (totalSend % 25000 == 0) {
			LOGGER.warn("dlms send repsonses to core " + Thread.currentThread().getName() + " " + totalSend );
		}
	}

	private void makeNewOsgpServiceClient(final RequestResponseMsg reqRespMsg) {
		if (reqRespMsg != null) {
			OsgpServiceClientPool.getInstance().getClient().addResponse(reqRespMsg);
		} else {
			OsgpServiceClientPool.getInstance().getClient();
		}
	}

	private void closeStream() {
		if (!alreadyClosed) {
			alreadyClosed = true;
			setComplete();
			shutdown();
		} else {
			LOGGER.warn("stream is closed in closeStream !?");
		}
	}
	

	public boolean isAlreadyClosed() {
		return alreadyClosed;
	}

}
