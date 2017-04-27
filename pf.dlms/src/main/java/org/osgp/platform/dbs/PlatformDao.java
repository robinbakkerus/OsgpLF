package org.osgp.platform.dbs;

import java.util.List;

import org.osgp.shared.dbs.UndeliveredTuple;
import org.osgp.util.dao.PK;

import com.alliander.osgp.shared.CorrelIdMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;

public interface PlatformDao {

	void saveRequestResponse(final RequestResponseMsg reqRespMsg);
	void saveRequestResponses(final List<RequestResponseMsg> reqRespMsg);
	
	RequestResponseMsg getResponse(final CorrelIdMsg responseCorrelIdMsg) throws InvalidProtocolBufferException;

	RequestResponseMsg getResponse(final String correlId) throws InvalidProtocolBufferException;

	void saveUndeliveredRequest(final RequestResponseMsg reqRespMsg);
	
	List<UndeliveredTuple> getAllUndeliveredRequests();

	void delete(PlatformTable table, PK pk);
	void deleteList(PlatformTable table, List<PK> pkList);
	
	Object get(PlatformTable table, PK pk);
	List<PK> getAllRequestResponseMsgPKs();
	List<RequestResponseMsg> getAllRequestResponseMsgs();

}
