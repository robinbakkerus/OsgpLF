package org.osgp.pa.dlms.application.dao;

import org.osgp.pa.dlms.application.dao.cassandra.DlmsDaoCassandraImpl;
import org.osgp.shared.dbs.Database;
import org.osgp.util.ConfigHelper;

public enum DlmsDaoFact {

	INSTANCE;
	
	private DlmsDao dao = null; 
	
	public DlmsDao getDao() {
		if (dao == null) {
			dao = makeDao();
		}
		return dao;
	}
	
	public void reset() {
		dao = makeDao();
	}
	
	private DlmsDao makeDao() {
		if (Database.Redis == ConfigHelper.getDatabaseImpl()) {
			dao = new DlmsDaoRedisImpl();
		} else if (Database.PERST == ConfigHelper.getDatabaseImpl()) {
			dao = new DlmsDaoPerstImpl();
		} else if (Database.Aerospike == ConfigHelper.getDatabaseImpl()) {
			dao = new DlmsDaoAerospikeImpl();
		} else {
			dao = new DlmsDaoCassandraImpl();
		}
		return dao;
	}
}

