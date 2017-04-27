package org.osgp.util;

import java.util.List;

import com.alliander.osgp.shared.RequestResponseMsg;

public class AllMsgWrapper {

	private List<RequestResponseMsg> allMsg;

	public AllMsgWrapper(List<RequestResponseMsg> allMsg) {
		super();
		this.allMsg = allMsg;
	}

	public List<RequestResponseMsg> getAllMsg() {
		return allMsg;
	}
	
}
