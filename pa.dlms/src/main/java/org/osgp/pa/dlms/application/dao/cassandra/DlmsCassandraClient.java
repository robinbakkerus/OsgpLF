package org.osgp.pa.dlms.application.dao.cassandra;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.osgp.shared.dbs.AbstractCassandraClient;
import org.osgp.util.dao.cassandra.CassandraField;
import org.osgp.util.dao.cassandra.CassandraFieldType;
import org.osgp.util.dao.cassandra.CassandraTableData;

import com.alliander.osgp.dlms.DlmsDeviceMsg;

public class DlmsCassandraClient extends AbstractCassandraClient {
	
	public static final String KEYSPACE_SM_INT = "Dlms";
	public static final String TBL_DLMS_DEVICE = "DlmsDevice";
	public static final String FLD_DEVICE_ID = "identification";
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
		TABLE_MAP.put(TBL_DLMS_DEVICE, 
				new CassandraTableData(DlmsDeviceMsg.class, TBL_DLMS_DEVICE)
				.addField(new CassandraField(FLD_DEVICE_ID, CassandraFieldType.STRING, true))
				.addField(new CassandraField(FLD_MSG, CassandraFieldType.BLOB)));
	}
	
	public static CassandraTableData getTableData(final String tableName) {
		return TABLE_MAP.get(tableName);
	}
}