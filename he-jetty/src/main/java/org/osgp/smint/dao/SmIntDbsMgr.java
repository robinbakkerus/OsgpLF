package org.osgp.smint.dao;

import org.osgp.shared.CC;
import org.osgp.shared.dbs.AbstractCassandraClient;
import org.osgp.shared.dbs.AbstractDbsMgr;
import org.osgp.shared.dbs.perst.AbstractPerstStorage;
import org.osgp.smint.dao.cassandra.SmIntCassandraClient;
import org.osgp.smint.dao.perst.SmIntPerstRoot;
import org.osgp.smint.dao.perst.SmIntPerstStorage;

public enum SmIntDbsMgr  {

	INSTANCE;
		
	private AbstractDbsMgr<SmIntPerstRoot> _dbsMgr = null;
	
	public void open() {
		_dbsMgr = new DbsMgr("localhost", 3000, CC.PERST_DBS_SMINT);
//		_dbsMgr.setupDatabase();
	}
	
	public void close() {
		_dbsMgr.closeDatabase();
	}
	
	public AbstractDbsMgr<SmIntPerstRoot> dbsMgr() {
		return _dbsMgr;
	}

	static class DbsMgr extends AbstractDbsMgr<SmIntPerstRoot> {
		public DbsMgr(String host, int aeroSpikePort, String perstDbname) {
			super(host, aeroSpikePort, perstDbname);
		}
		
		@Override
		protected AbstractPerstStorage<SmIntPerstRoot> makePerstRoot() {
			return new SmIntPerstStorage(perstDbname);
		}
		
		@Override
		protected AbstractCassandraClient getCassandraClient() {
			return new SmIntCassandraClient();
		}		
	}
}
