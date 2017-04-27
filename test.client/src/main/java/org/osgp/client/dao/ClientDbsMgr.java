package org.osgp.client.dao;

import org.osgp.client.dao.perst.ClientPerstRoot;
import org.osgp.client.dao.perst.ClientPerstStorage;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.AbstractCassandraClient;
import org.osgp.shared.dbs.AbstractDbsMgr;
import org.osgp.shared.dbs.perst.AbstractPerstStorage;

public enum ClientDbsMgr  {

	INSTANCE;
		
	private AbstractDbsMgr<ClientPerstRoot> _dbsMgr = null;
	
	public void open() {
		_dbsMgr = new DbsMgr("localhost", 3000, CC.PERST_DBS_CLIENT);
//		_dbsMgr.setupDatabase();
	}
	
	public void close() {
		_dbsMgr.closeDatabase();
	}
	
	public AbstractDbsMgr<ClientPerstRoot> dbsMgr() {
		return _dbsMgr;
	}

	static class DbsMgr extends AbstractDbsMgr<ClientPerstRoot> {
		public DbsMgr(String host, int aeroSpikePort, String perstDbname) {
			super(host, aeroSpikePort, perstDbname);
		}
		
		@Override
		protected AbstractPerstStorage<ClientPerstRoot> makePerstRoot() {
			return new ClientPerstStorage(perstDbname);
		}

		@Override
		protected AbstractCassandraClient getCassandraClient() {
			return new ClientCassandraClient();
		}
		
		
	}
}
