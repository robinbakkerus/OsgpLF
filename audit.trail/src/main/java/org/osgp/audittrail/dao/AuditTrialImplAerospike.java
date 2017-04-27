package org.osgp.audittrail.dao;

import java.util.List;

import org.osgp.shared.CC;
import org.osgp.util.dao.PK;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;

import static org.osgp.audittrail.dao.cassandra.AuditTrailCassandraClient.*;

public class AuditTrialImplAerospike implements AuditTrailDao, CC {

	// --- RequestResponseMsg

	public void saveRequestResponseMsg(final RequestResponseMsg reqRespMsg) {
		final String correlId = reqRespMsg.getCorrelId();
		Key key = new Key(KEYSPACE_AUDIT_TRAIL, TABLE_REQ_RESP, correlId);
		WritePolicy wPolicy = new WritePolicy();
		wPolicy.recordExistsAction = RecordExistsAction.UPDATE;
		dbsClient().put(wPolicy, key, new Bin(PB_DATA, reqRespMsg.toByteArray()));
	}

	public RequestResponseMsg getRequestResponseMsg(final String correlId) {
		Key key = new Key(KEYSPACE_AUDIT_TRAIL, TABLE_REQ_RESP, correlId);
		Record rec = dbsClient().get(null, key);
		try {
			return RequestResponseMsg.parseFrom((byte[]) rec.getValue(CC.PB_DATA));
		} catch (InvalidProtocolBufferException e) {
			return null;
		}
	}


	
	// --- delete

	@Override
	public List<PK> getAllRequestResponseMsgPKs() {
		return null;
	}

	@Override
	public void delete(PK pk) {
		dbsClient().delete(null, pk.aeroKey());
	}

	// ------------------

	
	private AerospikeClient dbsClient() {
		return AuditTrailDbsMgr.INSTANCE.dbsMgr().getAeroServer().getClient();
	}

}
