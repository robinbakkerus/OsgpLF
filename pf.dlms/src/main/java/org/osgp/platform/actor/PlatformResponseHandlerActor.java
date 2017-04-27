package org.osgp.platform.actor;

import org.osgp.platform.dbs.PlatformDao;
import org.osgp.platform.dbs.PlatformDaoFact;
import org.osgp.platform.rpc.client.PfOsgpServiceSmIntClient;
import org.osgp.platform.rpc.client.PfOsgpServiceSmIntClientFact;
import org.osgp.shared.CC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.RequestResponseListMsg;

import akka.actor.UntypedActor;

/**
 * Deze verwerkt het bericht dat terugkomt van de core. Het bericht wordt
 * opgeslagen, waarna later kan worden opgehaald. En hiermee is de cirkel rond.
 */
public class PlatformResponseHandlerActor extends UntypedActor implements CC {

	public static final String NAME = PlatformResponseHandlerActor.class.getSimpleName();
	private static final Logger LOGGER = LoggerFactory.getLogger(PlatformResponseHandlerActor.class);

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof RequestResponseListMsg) {
			handleResponse((RequestResponseListMsg) msg);
		}
	}

	private void handleResponse(final RequestResponseListMsg requestResponseListMsg) {
		dao().saveRequestResponses(requestResponseListMsg.getRequestResponsesList());
		this.sendResponsesToSmInt(requestResponseListMsg);
	}

	private PlatformDao dao() {
		return PlatformDaoFact.INSTANCE.getDao();
	}

	private void sendResponsesToSmInt(final RequestResponseListMsg requestResponseListMsg) {
		PfOsgpServiceSmIntClient client = PfOsgpServiceSmIntClientFact.client();
		if (client != null) {
			client.handleResponses(requestResponseListMsg);
		} else {
			LOGGER.error("no server to send RequestResponseListMsg to sm-integration ");
		}
	}
}
