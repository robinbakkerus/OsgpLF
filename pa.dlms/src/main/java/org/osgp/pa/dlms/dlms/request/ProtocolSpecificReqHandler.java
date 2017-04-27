package org.osgp.pa.dlms.dlms.request;

import java.util.List;

import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.pa.dlms.dlms.Dlms;
import org.osgp.pa.dlms.dlms.DlmsAkkaServer;
import org.osgp.pa.dlms.dlms.actor.DlmsDeviceActor;
import org.osgp.pa.dlms.dlms.stats.DlmsStatistics;
import org.osgp.shared.exceptionhandling.FunctionalException;
import org.osgp.util.AnnotRequestHandler;
import org.osgp.util.MsgMapper;
import org.osgp.util.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.ProtocolSpecificMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.ResponseMsg;
import com.alliander.osgp.shared.ResponseStatus;

import akka.actor.ActorRef;
import akka.actor.Props;

@AnnotRequestHandler(action=ProtocolSpecificMsg.class)
public class ProtocolSpecificReqHandler implements RequestHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolSpecificReqHandler.class);

	@Override
	public void ctorArguments(Object... args) {
		//Empty
	}


	@Override
	public void handleRequests(List<RequestResponseMsg> reqRespMsgs) {
		for (RequestResponseMsg reqRespMsg : reqRespMsgs) {
			DlmsDevice dlmsDevice = findDlmsDevice(reqRespMsg);
			if (dlmsDevice != null) {
				ActorRef actor = Dlms.actorSystem().actorOf(Props.create(DlmsDeviceActor.class));
				actor.tell(new DlmsRequestTuple(reqRespMsg, dlmsDevice) , ActorRef.noSender());
				DlmsStatistics.incRetryCount();
			}
		}
	}

	private DlmsDevice findDlmsDevice(final RequestResponseMsg reqRespMsg)  {
		try {
			return DlmsDevice.retrieve(reqRespMsg.getDevice());
		} catch (FunctionalException e) {
			ResponseMsg response = MsgMapper.simpleResponseMsg(ResponseStatus.NOT_OK, "functional.exception", "error", e.getMessage());
			RequestResponseMsg sendmsg = MsgMapper.makeSendReqRespToCore(reqRespMsg, response, false);
			DlmsAkkaServer.sendResponsesActor.tell(sendmsg, ActorRef.noSender());
			LOGGER.error("error find device {}",reqRespMsg.getDevice().getDeviceId(), e);
			return null;
		}
	}

}
