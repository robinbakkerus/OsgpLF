package org.osgp.core.request;

import java.util.List;
import java.util.stream.Collectors;

import org.osgp.core.rpc.OsgpServicePAClient;
import org.osgp.core.rpc.OsgpServicePAClientFact;
import org.osgp.util.AnnotRequestHandler;

import com.alliander.osgp.shared.AddDeviceActionMsg;
import com.alliander.osgp.shared.DeviceMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

@AnnotRequestHandler(action=AddDeviceActionMsg.class)
public class AddDeviceReqHandler extends AbstractReqHandler {

	@Override
	public void ctorArguments(Object... args) {
	}

	@Override
	public void handleRequests(List<RequestResponseMsg> reqRespMsgs) {
		final OsgpServicePAClient osgpService = OsgpServicePAClientFact.client();
		if (osgpService != null) {
			persistDevices(reqRespMsgs);
			reqRespMsgs.stream().forEach(f -> this.sendRequestToPA(osgpService, f));
			osgpService.setComplete();
		} else {
			LOGGER.error("Protocol adapter is not available" );
		}
	}

	private void persistDevices(List<RequestResponseMsg> reqRespMsgs) {
		List<DeviceMsg> deviceList = reqRespMsgs.stream().map(f -> f.getDevice()).collect(Collectors.toList());
		deviceDao().saveCoreDevices(deviceList);
	}	
	
	private void sendRequestToPA(final OsgpServicePAClient osgpService, RequestResponseMsg reqRespMsg) {
		osgpService.addNextRequest(reqRespMsg);
	}	
	

}
