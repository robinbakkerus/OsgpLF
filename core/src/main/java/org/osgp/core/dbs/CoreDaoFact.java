package org.osgp.core.dbs;

import org.osgp.shared.dbs.Database;
import org.osgp.util.ConfigHelper;

public enum CoreDaoFact {

	INSTANCE;
	
	private CoreDao dao = null; 
	
	public CoreDao getDao() {
		if (dao == null) {
			dao = makeDao();
		}
		return dao;
	}

	public void reset() {
		dao = makeDao();
	}
	
	private CoreDao makeDao() {
		if (Database.Redis == ConfigHelper.getDatabaseImpl()) {
			dao = new CoreDaoRedisImpl();
		} else if (Database.PERST == ConfigHelper.getDatabaseImpl()) {
			dao = new CoreDaoPerstImpl();
		} else if (Database.CASSANDRA == ConfigHelper.getDatabaseImpl()) {
			dao = new CoreDaoCassandraImpl();
		} else {
			dao = new CoreDaoAerospikeImpl();
		}
		return dao;
	}
}
