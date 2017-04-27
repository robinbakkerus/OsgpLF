package org.osgp.platform.dbs;

import org.osgp.platform.dbs.cassandra.PlatformDaoCassandraImpl;
import org.osgp.shared.dbs.Database;
import org.osgp.util.ConfigHelper;

public enum PlatformDaoFact {

	INSTANCE;
	
	private PlatformDao dao = null;
	
	public PlatformDao getDao() {
		if (dao == null) {
			dao = makeDao();
		}
		return dao;
	}
	
	public void reset() {
		dao = makeDao();
	}
	

	private PlatformDao makeDao() {
		if (Database.Redis == ConfigHelper.getDatabaseImpl()) {
			dao = new PlatformDaoRedisImpl();
		} else if (Database.PERST == ConfigHelper.getDatabaseImpl()) {
			dao = new PlatformDaoPerstImpl();
		} else if (Database.Aerospike == ConfigHelper.getDatabaseImpl()) {
			dao = new PlatformDaoAerospikeImpl();
		} else {
			dao = new PlatformDaoCassandraImpl();
		}
		return dao;
	}
}
