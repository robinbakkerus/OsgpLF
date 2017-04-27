package org.osgp.client.dao;

import org.osgp.shared.dbs.Database;
import org.osgp.util.ConfigHelper;

public enum ClientDaoFact {

	INSTANCE;
	
	private ClientDao dao = null;
	
	public ClientDao getDao() {
		if (dao == null) {
			dao =  makeDao();
		}
		return dao;
	}
	
	public void reset() {
		dao = makeDao();
	}
	
	private ClientDao makeDao() {
		if (Database.Redis == ConfigHelper.getDatabaseImpl()) {
			dao = new ClientDaoRedisImpl();
		} else if (Database.PERST == ConfigHelper.getDatabaseImpl()) {
			dao = new ClientDaoPerstImpl();
		} else {
			dao = new ClientDaoImplAerospike();
		}
		
		return dao;
	}
}
