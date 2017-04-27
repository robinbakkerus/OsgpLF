package org.osgp.platform.dbs;

import org.osgp.platform.dbs.cassandra.PlatformCassandraClient;
import org.osgp.platform.dbs.perst.PlatformPerstRoot;
import org.osgp.platform.dbs.perst.PlatformPerstStorage;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.AbstractCassandraClient;
import org.osgp.shared.dbs.AbstractDbsMgr;
import org.osgp.shared.dbs.perst.AbstractPerstStorage;

public enum PlatformDbsMgr  {

	INSTANCE;
		
	private AbstractDbsMgr<PlatformPerstRoot> _dbsMgr = null; 
	
	public void open() {
		_dbsMgr = new DbsMgr("localhost", 3000, CC.PERST_DBS_PLATFORM);
//		_dbsMgr.setupDatabase();
	}
	
	public void close() {
		_dbsMgr.closeDatabase();
	}
	
	public AbstractDbsMgr<PlatformPerstRoot> dbsMgr() {
		return _dbsMgr;
	}
	
	static class DbsMgr extends AbstractDbsMgr<PlatformPerstRoot> {
		public DbsMgr(String host, int aeroSpikePort, String perstDbname) {
			super(host, aeroSpikePort, perstDbname);
		}
		
		@Override
		protected AbstractPerstStorage<PlatformPerstRoot> makePerstRoot() {
			return new PlatformPerstStorage(perstDbname);
		}

		@Override
		protected AbstractCassandraClient getCassandraClient() {
			return new PlatformCassandraClient();
		}

		
	}
	
}
