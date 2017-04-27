package org.osgp.core.dbs;

import org.osgp.util.dao.PK;

import com.alliander.osgp.shared.RequestResponseMsg;

public class ScheduledTaskTuple {
	PK pk;
	RequestResponseMsg msg;
	
	public ScheduledTaskTuple(PK pk, RequestResponseMsg msg) {
		super();
		this.pk = pk;
		this.msg = msg;
	}

	public PK getPk() {
		return pk;
	}

	public RequestResponseMsg getMsg() {
		return msg;
	}
	
	
}
