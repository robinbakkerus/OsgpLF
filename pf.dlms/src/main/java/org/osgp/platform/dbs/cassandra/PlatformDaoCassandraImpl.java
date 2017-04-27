package org.osgp.platform.dbs.cassandra;

import static org.osgp.util.dao.cassandra.CassandraHelper.blob;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.osgp.platform.dbs.PlatformDao;
import org.osgp.platform.dbs.PlatformDbsMgr;
import org.osgp.platform.dbs.PlatformTable;
import org.osgp.shared.dbs.UndeliveredTuple;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.cassandra.CassandraHelper;

import com.alliander.osgp.shared.CorrelIdMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.datastax.driver.core.Session;
import com.google.protobuf.InvalidProtocolBufferException;

public class PlatformDaoCassandraImpl implements PlatformDao {

	
	final CassandraHelper<RequestResponseMsg> reqRespHelper = 
			new CassandraHelper<>(PlatformCassandraClient.getTableData(PlatformTable.REQ_RESP.getTableName()));
	final CassandraHelper<RequestResponseMsg> undelHelper = 
			new CassandraHelper<>(PlatformCassandraClient.getTableData(PlatformTable.CORE_UNDELIVERED.getTableName()));

	
	@Override
	public void saveRequestResponse(RequestResponseMsg reqRespMsg) {
		reqRespHelper.save(session(), reqRespMsg.getCorrelId(), blob(reqRespMsg) );
	}


	@Override
	public void saveRequestResponses(List<RequestResponseMsg> reqRespMsgList) {
		List<List<Object>> valuesList = new ArrayList<>();
		reqRespMsgList.stream().forEach(f -> valuesList.add(this.makeCoreDeviceObjectList(f)));
		reqRespHelper.saveList(session(), valuesList);
	}

	private List<Object> makeCoreDeviceObjectList(final RequestResponseMsg msg) {
		List<Object> r = new ArrayList<>();
		r.add(msg.getCorrelId());
		r.add(blob(msg));
		return r;
	}

	@Override
	public RequestResponseMsg getResponse(CorrelIdMsg responseCorrelIdMsg) throws InvalidProtocolBufferException {
		return getResponse(responseCorrelIdMsg.getCorrelid());
		
	}

	@Override
	public RequestResponseMsg getResponse(String correlId) throws InvalidProtocolBufferException {
		return reqRespHelper.select(session(), correlId);
	}

	@Override
	public void saveUndeliveredRequest(RequestResponseMsg reqRespMsg) {
		this.undelHelper.save(session(), reqRespMsg.getCorrelId(), blob(reqRespMsg) );
	}

	@Override
	public List<UndeliveredTuple> getAllUndeliveredRequests() {
		final List<RequestResponseMsg> allMsg = undelHelper.selectAll(session());
		return allMsg.stream().map(r -> new UndeliveredTuple(new PK(r.getCorrelId()), r)).collect(Collectors.toList());
	}

	@Override
	public void delete(final PlatformTable table, final PK pk) {
		helper(table).delete(session(), pk);
	}

	@Override
	public void deleteList(PlatformTable table, List<PK> pkList) {
		helper(table).deleteList(session(), pkList);
	}
	
	
	@Override
	public Object get(PlatformTable table, PK pk) {
		return null;
	}

	@Override
	public List<PK> getAllRequestResponseMsgPKs() {
		List<RequestResponseMsg> allMsg = reqRespHelper.selectAll(session());
		return allMsg.stream().map(r -> new PK(r.getCorrelId(), PlatformTable.REQ_RESP.getTableName())).collect(Collectors.toList());
	}

	@Override
	public List<RequestResponseMsg> getAllRequestResponseMsgs() {
		return reqRespHelper.selectAll(session());
	}

	// --------------

	private static Session session() {
		return PlatformDbsMgr.INSTANCE.dbsMgr().getCassandraSession();
	}
	
	private CassandraHelper<RequestResponseMsg> helper(final PlatformTable table) {
		if (PlatformTable.REQ_RESP == table) {
			return reqRespHelper;
		} else {
			return undelHelper;
		} 
	}
}
