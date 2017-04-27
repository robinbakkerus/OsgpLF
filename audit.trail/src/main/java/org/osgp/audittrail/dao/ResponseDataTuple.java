package org.osgp.audittrail.dao;

import java.util.Date;

import com.alliander.osgp.shared.RequestResponseMsg;

public class ResponseDataTuple {

	private String correlId;
	private Date createdAt;
	private RequestResponseMsg reqRespMsg;
	
	public ResponseDataTuple(String correlId, Date createdAt, RequestResponseMsg reqRespMsg) {
		super();
		this.correlId = correlId;
		this.createdAt = createdAt;
		this.reqRespMsg = reqRespMsg;
	}

	public String getCorrelId() {
		return correlId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public RequestResponseMsg getReqRespMsg() {
		return reqRespMsg;
	}

	
}
