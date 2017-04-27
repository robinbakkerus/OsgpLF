package org.osgp.client.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.osgp.shared.dbs.AbstractCassandraClient;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.cassandra.CassandraTableData;

public class ClientCassandraClient extends AbstractCassandraClient {
	


	public static final Map<String, CassandraTableData> TABLE_MAP = new HashMap<>();
	
	@Override
	protected String getKeyspaceName() {
		return null;
	}

	@Override
	protected Collection<CassandraTableData> getCassandraTables() {
		return TABLE_MAP.values();
	}

	public static CassandraTableData getTableData(final String tableName) {
		return TABLE_MAP.get(tableName);
	}
	
	public static String tableName(final PK pk) {
		return TABLE_MAP.get(pk.getTable()).getTableName();
	}
}

