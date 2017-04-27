package org.osgp.smint.service;

import java.util.ArrayList;
import java.util.List;

import org.osgp.dlms.MsgUtils;
import org.osgp.util.CorrelId;

import com.alliander.osgp.dlms.DlmsSpecificMsg;
import com.alliander.osgp.dlms.JobMsg;
import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.ResponseMsg;
import com.alliander.osgp.shared.ResponseStatus;

public class DevOpsBundler {

//	private static final Logger LOGGER = LoggerFactory.getLogger(DevOpsBundler.class);
	
	
	public List<RequestResponseMsg>  makeRequestResponses(final JobMsg jobMsg,final List<String> deviceIds) {
		List<RequestResponseMsg> reqRespList = new ArrayList<>(); 
		deviceIds.forEach(s -> reqRespList.add(makeDlmsReqRespMsg(jobMsg, s)));
		return reqRespList;
	}
	
	private RequestResponseMsg makeDlmsReqRespMsg(final JobMsg jobMsg, final String devid) {
		DlmsSpecificMsg specificMsg = DlmsSpecificMsg.newBuilder()
				.addAllActions(jobMsg.getActionsList())
				.build();

		return RequestResponseMsg.newBuilder()
				.setCommon(makeCommon(jobMsg, devid))
				.setCorrelId(CorrelId.generate())
				.setAction(MsgUtils.makeDlmsAction(specificMsg))
				.setResponse(makeResponseMsg())
				.build();
	}

	private CommonMsg makeCommon(final JobMsg jobMsg, final String devid) {
		return CommonMsg.newBuilder()
				.setApplicationName("Appname")
				.setOrganisation(jobMsg.getOrganisation())
				.setDeviceId(devid)
				.setJobId(jobMsg.getId())
				.setUserName("robinb").build();
	}
	
	private ResponseMsg makeResponseMsg() {
		return ResponseMsg.newBuilder().setAction("SpecificAction")
				.setStatus(ResponseStatus.SUBMITTED)
				.build();
	}

}
