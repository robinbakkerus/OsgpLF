package org.osgp.audittrail.dao;

import org.osgp.audittrail.dao.cassandra.AuditTrailCassandraClient;
import org.osgp.audittrail.dao.perst.AuditTrailPerstRoot;
import org.osgp.audittrail.dao.perst.AuditTrailPerstStorage;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.AbstractCassandraClient;
import org.osgp.shared.dbs.AbstractDbsMgr;
import org.osgp.shared.dbs.perst.AbstractPerstStorage;

public enum AuditTrailDbsMgr  {

	INSTANCE;
		
	private AbstractDbsMgr<AuditTrailPerstRoot> _dbsMgr = null; 
	
	public void open() {
		_dbsMgr = new DbsMgr("localhost", 3000, CC.PERST_DBS_AUDITRAIL);
//		_dbsMgr.setupDatabase();
	}
	
	public void close() {
		_dbsMgr.closeDatabase();
	}
	
	public AbstractDbsMgr<AuditTrailPerstRoot> dbsMgr() {
		return _dbsMgr;
	}

	static class DbsMgr extends AbstractDbsMgr<AuditTrailPerstRoot> {
		public DbsMgr(String host, int aeroSpikePort, String perstDbname) {
			super(host, aeroSpikePort, perstDbname);
		}

		@Override
		protected AbstractPerstStorage<AuditTrailPerstRoot> makePerstRoot() {
			return new AuditTrailPerstStorage(perstDbname);
		}

		@Override
		protected AbstractCassandraClient getCassandraClient() {
			return new AuditTrailCassandraClient();
		}
	}
	
}
