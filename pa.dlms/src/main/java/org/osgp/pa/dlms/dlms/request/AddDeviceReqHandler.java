package org.osgp.pa.dlms.dlms.request;

import java.util.List;
import java.util.stream.Collectors;

import org.osgp.pa.dlms.application.dao.DlmsDao;
import org.osgp.pa.dlms.application.dao.DlmsDaoFact;
import org.osgp.util.AnnotRequestHandler;
import org.osgp.util.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DlmsDeviceMsg;
import com.alliander.osgp.shared.AddDeviceActionMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

@AnnotRequestHandler(action=AddDeviceActionMsg.class)
public class AddDeviceReqHandler implements RequestHandler {

	private static Logger LOGGER = LoggerFactory.getLogger(AddDeviceReqHandler.class);
	
	@Override
	public void ctorArguments(Object... args) {
	}

	@Override
	public void handleRequests(List<RequestResponseMsg> reqRespMsgList) {
		List<DlmsDeviceMsg> dlmsDeviceList = reqRespMsgList.stream().map(f -> this.makeDlmsDeviceMsg(f)).collect(Collectors.toList());
		dao().saveList(dlmsDeviceList);
	}
	
	private DlmsDeviceMsg makeDlmsDeviceMsg(final RequestResponseMsg reqRespMsg) {
		try {
			AddDeviceActionMsg addDeviceMsg = reqRespMsg.getAction().getAddDevice();
			return DlmsDeviceMsg.parseFrom((byte[]) addDeviceMsg.getProtocolSpecific().getRaw().toByteArray());
		} catch(Exception e) {
			LOGGER.error("error saving dlsm devices " + e, e);
			return null;
		} 
	}

	//----
	private DlmsDao dao() {
		return DlmsDaoFact.INSTANCE.getDao();
	}
}
