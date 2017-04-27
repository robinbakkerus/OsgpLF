package org.osgp.audittrail.dao;

import java.util.List;

import org.osgp.util.dao.PK;

import com.alliander.osgp.shared.RequestResponseMsg;

public interface AuditTrailDao {

	void saveRequestResponseMsg(final RequestResponseMsg reqRespMsg);

	RequestResponseMsg getRequestResponseMsg(final String correlId);
	
	void delete(PK pk);
	
	List<PK> getAllRequestResponseMsgPKs();
}

