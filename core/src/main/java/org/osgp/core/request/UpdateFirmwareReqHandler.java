package org.osgp.core.request;

import java.util.List;

import org.osgp.util.AnnotRequestHandler;

import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.UpdateFirmwareActionMsg;

@AnnotRequestHandler(action=UpdateFirmwareActionMsg.class)
public class UpdateFirmwareReqHandler  extends AbstractReqHandler {
	
	@Override
	public void ctorArguments(Object... args) {
	}


	@Override
	public void handleRequests(List<RequestResponseMsg> reqRespMsgs) {
		// TODO Auto-generated method stub
	}

}
