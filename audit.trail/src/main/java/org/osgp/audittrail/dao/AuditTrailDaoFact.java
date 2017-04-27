package org.osgp.audittrail.dao;

import org.osgp.shared.dbs.Database;
import org.osgp.util.ConfigHelper;

public enum AuditTrailDaoFact {
	
	INSTANCE;
	
	private AuditTrailDao dao = null;
	
	public AuditTrailDao getDao() {
		if (dao == null) {
			dao = makeDao();
		}
		return dao;
	}

	public void reset() {
		dao = makeDao();
	}
	
	private AuditTrailDao makeDao() {
		AuditTrailDao r = null;
		if (Database.Redis == ConfigHelper.getDatabaseImpl()) {
			r = new AuditTrailRedisImpl();
		} else if (Database.PERST == ConfigHelper.getDatabaseImpl()) {
			r = new AuditTrailPerstImpl();
		} else {
			r = new AuditTrialImplAerospike();
		}
		return r;
	}
}
