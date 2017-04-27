package org.osgp.audittrail.dao.cassandra;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.osgp.shared.dbs.AbstractCassandraClient;
import org.osgp.util.dao.cassandra.CassandraField;
import org.osgp.util.dao.cassandra.CassandraFieldType;
import org.osgp.util.dao.cassandra.CassandraTableData;

import com.alliander.osgp.shared.RequestResponseMsg;

public class AuditTrailCassandraClient extends AbstractCassandraClient {
	
	public static final String KEYSPACE_AUDIT_TRAIL = "audit_trail";
	public static final String TABLE_REQ_RESP = "request_responses";
	public static final String FLD_CORREL_ID = "correlId";
	public static final String FLD_MSG = "msg";
	
	public static final Map<String, CassandraTableData> TABLE_MAP = new HashMap<>();
	
	@Override
	protected String getKeyspaceName() {
		return KEYSPACE_AUDIT_TRAIL;
	}

	@Override
	protected Collection<CassandraTableData> getCassandraTables() {
		return TABLE_MAP.values();
	}

	static {
		TABLE_MAP.put(TABLE_REQ_RESP, 
				new CassandraTableData(RequestResponseMsg.class, TABLE_REQ_RESP)
				.addField(new CassandraField(FLD_CORREL_ID, CassandraFieldType.UUID, true))
				.addField(new CassandraField(FLD_MSG, CassandraFieldType.BLOB)));
	}
	
	public static CassandraTableData getTableData(final String tableName) {
		return TABLE_MAP.get(tableName);
	}

}