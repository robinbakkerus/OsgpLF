package org.osgp.smint.actor;

import java.util.List;

import com.alliander.osgp.shared.RequestResponseMsg;

public class RequestResponseMsgWrapper {

	private final List<RequestResponseMsg> requests;
	
	public RequestResponseMsgWrapper(final List<RequestResponseMsg> requests) {
		super();
		this.requests = requests;
	}

	public List<RequestResponseMsg> getRequests() {
		return requests;
	}
	
	
}
