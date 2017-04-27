package org.osgp.pa.dlms.application.dao;

import org.osgp.pa.dlms.application.dao.cassandra.DlmsCassandraClient;
import org.osgp.pa.dlms.application.dao.perst.DlmsPerstRoot;
import org.osgp.pa.dlms.application.dao.perst.DlmsPerstStorage;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.AbstractCassandraClient;
import org.osgp.shared.dbs.AbstractDbsMgr;
import org.osgp.shared.dbs.perst.AbstractPerstStorage;

public enum DlmsDbsMgr  {

	INSTANCE;
		
	private AbstractDbsMgr<DlmsPerstRoot> _dbsMgr = null; 
	
	public void open() {
		_dbsMgr = new DbsMgr("localhost", 3000, CC.PERST_DBS_DLMS);
//		_dbsMgr.setupDatabase();
	}
	
	public void close() {
		_dbsMgr.closeDatabase();
	}
	
	public AbstractDbsMgr<DlmsPerstRoot> dbsMgr() {
		return _dbsMgr;
	}

	static class DbsMgr extends AbstractDbsMgr<DlmsPerstRoot> {
		public DbsMgr(String host, int aeroSpikePort, String perstDbname) {
			super(host, aeroSpikePort, perstDbname);
		}
		
		@Override
		protected AbstractPerstStorage<DlmsPerstRoot> makePerstRoot() {
			return new DlmsPerstStorage(perstDbname);
		}

		@Override
		protected AbstractCassandraClient getCassandraClient() {
			return new DlmsCassandraClient();
		}
		
		
	}
}
