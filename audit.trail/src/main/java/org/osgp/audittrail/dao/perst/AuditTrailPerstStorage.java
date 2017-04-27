package org.osgp.audittrail.dao.perst;

import org.osgp.shared.dbs.perst.AbstractPerstStorage;

public class AuditTrailPerstStorage extends AbstractPerstStorage<AuditTrailPerstRoot>{

	public AuditTrailPerstStorage(String dbsName) {
		super(dbsName);
	}

	@Override
	protected AuditTrailPerstRoot makeRoot() {
		System.out.println("AuditTrailPerstRoot.makeRoot");
		return new AuditTrailPerstRoot(getStorage());
	}
	
	

}
