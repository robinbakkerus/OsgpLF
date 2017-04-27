package org.osgp.platform.dbs.cassandra;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.osgp.platform.dbs.PlatformTable;
import org.osgp.shared.dbs.AbstractCassandraClient;
import org.osgp.util.dao.cassandra.CassandraField;
import org.osgp.util.dao.cassandra.CassandraFieldType;
import org.osgp.util.dao.cassandra.CassandraTableData;

import com.alliander.osgp.shared.RequestResponseMsg;

public class PlatformCassandraClient extends AbstractCassandraClient {
	
	public static final String KEYSPACE_PLATFORM = "Platform";
	public static final String FLD_CORREL_ID = "correlId";
	public static final String FLD_MSG = "msg";
	
	protected static final Map<String, CassandraTableData> TABLE_MAP = new HashMap<>();
	
	@Override
	protected String getKeyspaceName() {
		return KEYSPACE_PLATFORM;
	}

	@Override
	protected Collection<CassandraTableData> getCassandraTables() {
		return TABLE_MAP.values();
	}

	static {
		TABLE_MAP.put(PlatformTable.REQ_RESP.getTableName(), 
				new CassandraTableData(RequestResponseMsg.class, PlatformTable.REQ_RESP.getTableName())
				.addField(new CassandraField(FLD_CORREL_ID, CassandraFieldType.UUID, true))
				.addField(new CassandraField(FLD_MSG, CassandraFieldType.BLOB)));
		TABLE_MAP.put(PlatformTable.CORE_UNDELIVERED.getTableName(), 
				new CassandraTableData(RequestResponseMsg.class, PlatformTable.CORE_UNDELIVERED.getTableName())
				.addField(new CassandraField(FLD_CORREL_ID, CassandraFieldType.UUID, true))
				.addField(new CassandraField(FLD_MSG, CassandraFieldType.BLOB)));
	}
	
	public static CassandraTableData getTableData(final String tableName) {
		return TABLE_MAP.get(tableName);
	}

}

