package org.osgp.core.request;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.List;

import org.osgp.core.AkkaCoreSystem;
import org.osgp.core.Core;
import org.osgp.core.actor.RetryDlmsActor;
import org.osgp.core.rpc.OsgpServicePAClient;
import org.osgp.core.rpc.OsgpServicePAClientFact;
import org.osgp.core.rpc.OsgpServicePFClient;
import org.osgp.core.rpc.OsgpServicePFClientFact;
import org.osgp.core.stats.CoreStatistics;
import org.osgp.util.AllMsgWrapper;
import org.osgp.util.AnnotRequestHandler;
import org.osgp.util.MsgMapper;
import org.osgp.util.ShowProgress;

import com.alliander.osgp.shared.DeviceMsg;
import com.alliander.osgp.shared.ProtocolSpecificMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.ResponseMsg;
import com.alliander.osgp.shared.ResponseStatus;

import akka.actor.ActorRef;
import akka.actor.Props;

@AnnotRequestHandler(action=ProtocolSpecificMsg.class)
public class ProtocolSpecificReqHandler  extends AbstractReqHandler  {

	@Override
	public void ctorArguments(Object... args) {
		//Empty
	}


	@Override
	public void handleRequests(List<RequestResponseMsg> allReqRespMsgs) {
		LOGGER.debug("ProtocolSpecificReqHandler.handleRequests called ");
		final OsgpServicePAClient osgpService = OsgpServicePAClientFact.client();
		if (osgpService != null) {
			allReqRespMsgs.forEach(reqRespMsg -> this.handleRequest(osgpService, reqRespMsg));
			osgpService.setComplete();
		} else {
			final ActorRef actor = Core.actorSystem().actorOf(Props.create(RetryDlmsActor.class, AkkaCoreSystem.headActor()));
			actor.tell(new AllMsgWrapper(allReqRespMsgs), ActorRef.noSender());
		}
	}
	
	private void handleRequest(final OsgpServicePAClient osgpService, RequestResponseMsg reqRespMsg) {
		DeviceMsg device = deviceDao().findDevice(reqRespMsg);
		if (device != null) {
			if (isValidOrganisation(reqRespMsg, device)) {
				RequestResponseMsg updmsg = RequestResponseMsg.newBuilder(reqRespMsg).setDevice(device).build();
				osgpService.addNextRequest(updmsg);
				CoreStatistics.incRequestsOut();
				ShowProgress.clock("CoreRequests");
			} else {
				ResponseMsg respMsg = MsgMapper.simpleResponseMsg(ResponseStatus.NOT_OK, "invalid.organisation", 
						"device", reqRespMsg.getCommon().getDeviceId());
				handleError(reqRespMsg, respMsg);
			}
		} else {
			ResponseMsg respMsg = MsgMapper.simpleResponseMsg(ResponseStatus.NOT_OK, "device.notfound", 
					"device", reqRespMsg.getCommon().getDeviceId());
			handleError(reqRespMsg, respMsg);
		}
	}
	
	private void handleError(final RequestResponseMsg reqRespMsg, final ResponseMsg respMsg) {
		LOGGER.error("error handling request for {}", reqRespMsg.getCommon().getDeviceId());
		final OsgpServicePFClient platformSrv = OsgpServicePFClientFact.client();	
		RequestResponseMsg addmsg = RequestResponseMsg.newBuilder(reqRespMsg)
				.setResponse(respMsg)
				.build();
		platformSrv.addResponse(RequestResponseMsg.newBuilder(addmsg).build());
		CoreStatistics.incResponsesOut();
		CoreStatistics.incErrCount();
		platformSrv.setComplete();
	}
	
	private boolean isValidOrganisation(final RequestResponseMsg reqRespMsg, final DeviceMsg deviceMsg) {
		return deviceMsg.getOrganisations().indexOf(reqRespMsg.getCommon().getOrganisation()) >= 0;
	}

}
