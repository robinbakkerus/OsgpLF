package org.osgp.platform.actor;

import java.util.List;

import org.osgp.platform.AkkaPlatformSystem;
import org.osgp.platform.rpc.client.PfAuditTrailServiceClient;
import org.osgp.platform.rpc.client.PfAuditTrailServiceClientFact;
import org.osgp.platform.rpc.client.PfOsgpServiceCoreClient;
import org.osgp.platform.rpc.client.PfOsgpServiceCoreClientFact;
import org.osgp.platform.stats.PlatformStatistics;
import org.osgp.shared.CC;
import org.osgp.util.AllMsgWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.RequestResponseListMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * Deze verwerkt het bericht dat terugkomt van de core. Het bericht wordt
 * opgeslagen, waarna later kan worden opgehaald. En hiermee is de cirkel rond.
 */
public class PlatformRequestHandlerActor extends UntypedActor implements CC {

	public static final String NAME = PlatformRequestHandlerActor.class.getSimpleName();
	private static final Logger LOGGER = LoggerFactory.getLogger(PlatformRequestHandlerActor.class);

	@Override
	public void onReceive(final Object msg) {
		if (msg instanceof RequestResponseListMsg) {
			handleRequests((RequestResponseListMsg) msg);
		}
	}

	private void handleRequests(final RequestResponseListMsg requestResponseListMsg) {
		final List<RequestResponseMsg> allReqResp = requestResponseListMsg.getRequestResponsesList();
//		sendRequestsToAuditTrail(allReqResp);
		this.sendRequestsToCore(allReqResp);
	}
	
	private void sendRequestsToAuditTrail(final List<RequestResponseMsg> allReqResp) {
		final PfAuditTrailServiceClient auditTrailSrv = PfAuditTrailServiceClientFact.client();
		if (auditTrailSrv != null) {
			for (RequestResponseMsg reqRespMsg : allReqResp) {
				auditTrailSrv.addNextRequest(RequestResponseMsg.newBuilder(reqRespMsg).build());
			}
			auditTrailSrv.setComplete();
		} else {
			LOGGER.warn("No AuditTrail service available");
		}
	
	}
	
	private void sendRequestsToCore(final List<RequestResponseMsg> allReqResp) {
		final PfOsgpServiceCoreClient osgpService = PfOsgpServiceCoreClientFact.client();
//		this.sendRequestsToAuditTrail(allReqResp);
		if (osgpService != null) {
			allReqResp.forEach(f -> this.sendRequestToCore(f, osgpService));
			osgpService.setComplete();
		} else {
			final ActorRef actor = AkkaPlatformSystem.actorSystem().actorOf(Props.create(RetryCoreActor.class, AkkaPlatformSystem.headActor()));
			actor.tell(new AllMsgWrapper(allReqResp), ActorRef.noSender());
		}
	}
	
	private void sendRequestToCore(final RequestResponseMsg reqRespMsg, final PfOsgpServiceCoreClient osgpService) {
		osgpService.addNextRequest(RequestResponseMsg.newBuilder(reqRespMsg).build());
		PlatformStatistics.incRequestsOut();
	}


}
