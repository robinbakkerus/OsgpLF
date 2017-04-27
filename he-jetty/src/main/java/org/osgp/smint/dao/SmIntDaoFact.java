package org.osgp.smint.dao;

import org.osgp.shared.dbs.Database;
import org.osgp.smint.dao.cassandra.SmIntDaoCassandraImpl;
import org.osgp.util.ConfigHelper;

public enum SmIntDaoFact {

	INSTANCE;
	
	private SmIntDao dao = null;
	
	public SmIntDao getDao() {
		if (dao == null) {
			dao =  makeDao();
		}
		return dao;
	}
	
	public void reset() {
		dao = makeDao();
	}
	
	private SmIntDao makeDao() {
		if (Database.Redis == ConfigHelper.getDatabaseImpl()) {
			dao = new SmIntDaoRedisImpl();
		} else if (Database.PERST == ConfigHelper.getDatabaseImpl()) {
			dao = new SmIntDaoPerstImpl();
		} else if (Database.CASSANDRA == ConfigHelper.getDatabaseImpl()) {
			dao = new SmIntDaoCassandraImpl();
		} else {
			dao = new SmIntDaoImplAerospike();
		}
		
		return dao;
	}
}
