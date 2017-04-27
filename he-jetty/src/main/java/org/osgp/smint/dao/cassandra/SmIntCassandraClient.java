package org.osgp.smint.dao.cassandra;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.osgp.shared.dbs.AbstractCassandraClient;
import org.osgp.smint.dao.SmIntTable;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.cassandra.CassandraField;
import org.osgp.util.dao.cassandra.CassandraFieldType;
import org.osgp.util.dao.cassandra.CassandraTableData;

import com.alliander.osgp.dlms.DeviceGroupMsg;
import com.alliander.osgp.dlms.JobMsg;
import com.alliander.osgp.dlms.RecipeMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

public class SmIntCassandraClient extends AbstractCassandraClient {
	
	public static final String KEYSPACE_SM_INT = "SmInt";
	public static final String FLD_CORREL_ID = "correlId";
	public static final String FLD_MSG = "msg";
	public static final String FLD_ID = "id";

	protected static final Map<String, CassandraTableData> TABLE_MAP = new HashMap<>();
	
	@Override
	protected String getKeyspaceName() {
		return KEYSPACE_SM_INT;
	}

	@Override
	protected Collection<CassandraTableData> getCassandraTables() {
		return TABLE_MAP.values();
	}

	static {
		TABLE_MAP.put(SmIntTable.RR_NEW.getTableName(), 
				new CassandraTableData(RequestResponseMsg.class, SmIntTable.RR_NEW.getTableName())
				.addField(new CassandraField(FLD_CORREL_ID, CassandraFieldType.UUID, true))
				.addField(new CassandraField(FLD_MSG, CassandraFieldType.BLOB)));
		TABLE_MAP.put(SmIntTable.RR_SEND.getTableName(), 
				new CassandraTableData(RequestResponseMsg.class, SmIntTable.RR_SEND.getTableName())
				.addField(new CassandraField(FLD_CORREL_ID, CassandraFieldType.UUID, true))
				.addField(new CassandraField(FLD_MSG, CassandraFieldType.BLOB)));
		TABLE_MAP.put(SmIntTable.RR_DONE.getTableName(), 
				new CassandraTableData(RequestResponseMsg.class, SmIntTable.RR_DONE.getTableName())
				.addField(new CassandraField(FLD_CORREL_ID, CassandraFieldType.UUID, true))
				.addField(new CassandraField(FLD_MSG, CassandraFieldType.BLOB)));
		TABLE_MAP.put(SmIntTable.JOB.getTableName(), 
				new CassandraTableData(JobMsg.class, SmIntTable.JOB.getTableName())
				.addField(new CassandraField(FLD_ID, CassandraFieldType.LONG, true))
				.addField(new CassandraField(FLD_MSG, CassandraFieldType.BLOB)));
		TABLE_MAP.put(SmIntTable.DEV_GROUP.getTableName(), 
				new CassandraTableData(DeviceGroupMsg.class, SmIntTable.DEV_GROUP.getTableName())
				.addField(new CassandraField(FLD_ID, CassandraFieldType.LONG, true))
				.addField(new CassandraField(FLD_MSG, CassandraFieldType.BLOB)));
		TABLE_MAP.put(SmIntTable.RECIPE.getTableName(), 
				new CassandraTableData(RecipeMsg.class, SmIntTable.RECIPE.getTableName())
				.addField(new CassandraField(FLD_ID, CassandraFieldType.LONG, true))
				.addField(new CassandraField(FLD_MSG, CassandraFieldType.BLOB)));
	}
	
	public static CassandraTableData getTableData(final String tableName) {
		return TABLE_MAP.get(tableName);
	}
	
	public static String tableName(final PK pk) {
		return TABLE_MAP.get(pk.getTable()).getTableName();
	}

	public static String pkField(final PK pk) {
		return TABLE_MAP.get(pk.getTable()).getFields().get(0).getName();
	}
}

