package org.osgp.pa.dlms.dlms.actor;

import org.osgp.pa.dlms.rpc.OsgpServiceClientPool;
import org.osgp.pa.dlms.rpc.OsgpServiceCoreClient;
import org.osgp.shared.CC;

import com.alliander.osgp.shared.RequestResponseMsg;

import akka.actor.UntypedActor;

/**
 * Deze actor verzameld reponses van DlmsDeviceActor en stuurt die als een stream naar de Core laag
 */
public class SendResponsesActor extends UntypedActor implements CC {

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof RequestResponseMsg) {
			handleRequest((RequestResponseMsg) msg);
		}
	}

	private void handleRequest(RequestResponseMsg reqRespMsg) {
		final OsgpServiceCoreClient client = OsgpServiceClientPool.getInstance().getClient();
		if (client != null) {
			client.addResponse(reqRespMsg);
		} else {
			//TODO save in not-available tabel
		}
	}

	
}
