package org.osgp.shared.dbs;

import org.osgp.util.dao.PK;

import com.alliander.osgp.shared.RequestResponseMsg;

public class UndeliveredTuple {

	private PK pk;
	private RequestResponseMsg reqRespMsg;
	
	public UndeliveredTuple(PK pk, RequestResponseMsg reqRespMsg) {
		super();
		this.pk = pk;
		this.reqRespMsg = reqRespMsg;
	}

	public PK getPk() {
		return pk;
	}

	public RequestResponseMsg getReqRespMsg() {
		return reqRespMsg;
	}
	
}
