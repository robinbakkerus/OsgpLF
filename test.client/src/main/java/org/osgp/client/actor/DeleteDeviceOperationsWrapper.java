package org.osgp.client.actor;

import java.util.List;

import com.alliander.osgp.shared.RequestResponseMsg;

public class DeleteDeviceOperationsWrapper {

	private List<RequestResponseMsg> requests;

	public DeleteDeviceOperationsWrapper(List<RequestResponseMsg> requests) {
		super();
		this.requests = requests;
	}

	public List<RequestResponseMsg> getRequests() {
		return requests;
	}
	
	
}
