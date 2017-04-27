package org.osgp.audittrail.dao.cassandra;

import static org.osgp.audittrail.dao.cassandra.AuditTrailCassandraClient.FLD_CORREL_ID;
import static org.osgp.audittrail.dao.cassandra.AuditTrailCassandraClient.TABLE_REQ_RESP;
import static org.osgp.util.dao.cassandra.CassandraHelper.blob;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.osgp.audittrail.dao.AuditTrailDao;
import org.osgp.audittrail.dao.AuditTrailDbsMgr;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.cassandra.CassandraHelper;

import com.alliander.osgp.shared.RequestResponseMsg;
import com.datastax.driver.core.Session;

public class AuditTrailCassandraImpl implements AuditTrailDao {

	final CassandraHelper<RequestResponseMsg> reqRespHelper = new CassandraHelper<RequestResponseMsg>(
		AuditTrailCassandraClient.getTableData(TABLE_REQ_RESP));

	@Override
	public void saveRequestResponseMsg(RequestResponseMsg reqRespMsg) {
		reqRespHelper.save(session(), reqRespMsg.getCorrelId(), blob(reqRespMsg));
	}

	@Override
	public RequestResponseMsg getRequestResponseMsg(String correlId) {
		return reqRespHelper.select(session(), correlId);
	}

	@Override
	public void delete(PK pk) {
		UUID correlId = UUID.fromString(pk.getKey().toString());
		String qry = String.format("DELETE FROM %s WHERE %s = ?", pk.getTable(), FLD_CORREL_ID);
		session().execute(qry, correlId);
	}

	@Override
	public List<PK> getAllRequestResponseMsgPKs() {
		List<RequestResponseMsg> allMsg = reqRespHelper.selectAll(session());
		return allMsg.stream().map(r -> new PK(r.getCorrelId(), TABLE_REQ_RESP)).collect(Collectors.toList());
	}

	// --------------

	private static Session session() {
		return AuditTrailDbsMgr.INSTANCE.dbsMgr().getCassandraSession();
	}

}
