package org.osgp.at.test;

import org.junit.Test;
import org.osgp.audittrail.dao.AuditTrailDaoFact;
import org.osgp.audittrail.dao.AuditTrailDbsMgr;

public class TestDbsFactories {

	@Test
	public void test() throws InterruptedException {
		AuditTrailDbsMgr.INSTANCE.open();
		AuditTrailDaoFact.INSTANCE.getDao().getAllRequestResponseMsgPKs();
		AuditTrailDbsMgr.INSTANCE.close();
	}

}
