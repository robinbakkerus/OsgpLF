package org.osgp.audittrail.dao.perst;

import org.garret.perst.FieldIndex;
import org.garret.perst.Persistent;
import org.garret.perst.Storage;
import org.osgp.shared.CC;

public class AuditTrailPerstRoot extends Persistent implements CC {

	public FieldIndex<PerstAuditTrailMsg> auditTrailIndex;

	public AuditTrailPerstRoot(Storage storage) {
		super(storage);
		auditTrailIndex = storage.<PerstAuditTrailMsg> createFieldIndex(PerstAuditTrailMsg.class, "strKey", true);
	}
	
}
