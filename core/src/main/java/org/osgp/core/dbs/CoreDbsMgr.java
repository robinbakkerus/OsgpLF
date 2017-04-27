package org.osgp.core.dbs;

import org.osgp.core.dbs.perst.CorePerstRoot;
import org.osgp.core.dbs.perst.CorePerstStorage;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.AbstractCassandraClient;
import org.osgp.shared.dbs.AbstractDbsMgr;
import org.osgp.shared.dbs.perst.AbstractPerstStorage;

public enum CoreDbsMgr  {

	INSTANCE;
		
	private AbstractDbsMgr<CorePerstRoot> _dbsMgr = null; 
	
	public void open() {
		_dbsMgr = new DbsMgr("localhost", 3000, CC.PERST_DBS_CORE);
//		_dbsMgr.setupDatabase();
	}
	
	public void close() {
		_dbsMgr.closeDatabase();
	}
	
	public AbstractDbsMgr<CorePerstRoot> dbsMgr() {
		return _dbsMgr;
	}

	static class DbsMgr extends AbstractDbsMgr<CorePerstRoot> {
		public DbsMgr(String host, int aeroSpikePort, String perstDbname) {
			super(host, aeroSpikePort, perstDbname);
		}
		
		@Override
		protected AbstractPerstStorage<CorePerstRoot> makePerstRoot() {
			return new CorePerstStorage(perstDbname);
		}

		@Override
		protected AbstractCassandraClient getCassandraClient() {
			return new CoreCassandraClient();
		}
		
		
	}
	
}
