package org.osgp.core.dbs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.osgp.shared.dbs.AbstractCassandraClient;
import org.osgp.util.dao.cassandra.CassandraField;
import org.osgp.util.dao.cassandra.CassandraFieldType;
import org.osgp.util.dao.cassandra.CassandraTableData;

import com.alliander.osgp.shared.DeviceMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

public class CoreCassandraClient extends AbstractCassandraClient {
	
	public static final String KEYSPACE_SM_INT = "core";
	public static final String TABLE_CORE_DEVICE = "core_device";
	public static final String TABLE_CORE_SCHEDULE = "core_schedule";
	public static final String TABLE_UNDELIVERED_DLMS = "undelivered_core_pa";
	public static final String BIN_UNDELIVERED_DLMS = "ud_core_dlms";	
	public static final String FLD_CORREL_ID = "correlId";
	public static final String FLD_DEVICE_NAME = "deviceName";
	public static final String FLD_MSG = "msg";
	
	public static final Map<String, CassandraTableData> TABLE_MAP = new HashMap<>();

	@Override
	protected String getKeyspaceName() {
		return KEYSPACE_SM_INT;
	}

	@Override
	protected Collection<CassandraTableData> getCassandraTables() {
		return TABLE_MAP.values();
	}

	static {
		TABLE_MAP.put(TABLE_CORE_SCHEDULE, 
				new CassandraTableData(RequestResponseMsg.class, TABLE_CORE_SCHEDULE)
				.addField(new CassandraField(FLD_CORREL_ID, CassandraFieldType.UUID, true))
				.addField(new CassandraField(FLD_MSG, CassandraFieldType.BLOB)));

		TABLE_MAP.put(TABLE_UNDELIVERED_DLMS, 
				new CassandraTableData(RequestResponseMsg.class, TABLE_UNDELIVERED_DLMS)
				.addField(new CassandraField(FLD_CORREL_ID, CassandraFieldType.UUID, true))
				.addField(new CassandraField(FLD_MSG, CassandraFieldType.BLOB)));
		
		TABLE_MAP.put(TABLE_CORE_DEVICE, 
				new CassandraTableData(DeviceMsg.class, TABLE_CORE_DEVICE)
				.addField(new CassandraField(FLD_DEVICE_NAME, CassandraFieldType.STRING, true))
				.addField(new CassandraField(FLD_MSG, CassandraFieldType.BLOB)));
	}
	
	public static CassandraTableData getTableData(final String tableName) {
		return TABLE_MAP.get(tableName);
	}
}
