package org.osgp.pa.dlms.dlms.request;

import java.util.List;

import org.osgp.util.AnnotRequestHandler;
import org.osgp.util.RequestHandler;

import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.UpdateFirmwareActionMsg;

@AnnotRequestHandler(action=UpdateFirmwareActionMsg.class)
public class UpdateFirmwareReqHandler implements RequestHandler {

	@Override
	public void ctorArguments(Object... args) {
	}

	@Override
	public void handleRequests(List<RequestResponseMsg> reqRespMsgs) {
		// TODO Auto-generated method stub
	}

}
