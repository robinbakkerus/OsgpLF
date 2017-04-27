package org.osgp.pa.dlms.dlms.request;

import org.osgp.pa.dlms.application.services.DlmsDevice;

import com.alliander.osgp.shared.RequestResponseMsg;

public class DlmsRequestTuple {

	private RequestResponseMsg reqRespMsg;
	private DlmsDevice dlmsDevice;
	
	public DlmsRequestTuple(RequestResponseMsg reqRespMsg, DlmsDevice dlmsDevice) {
		super();
		this.reqRespMsg = reqRespMsg;
		this.dlmsDevice = dlmsDevice;
	}

	public RequestResponseMsg getReqRespMsg() {
		return reqRespMsg;
	}

	public DlmsDevice getDlmsDevice() {
		return dlmsDevice;
	}
	
}
