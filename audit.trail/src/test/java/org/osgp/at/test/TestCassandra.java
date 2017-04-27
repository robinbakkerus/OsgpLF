package org.osgp.at.test;

import java.util.List;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgp.audittrail.dao.AuditTrailDao;
import org.osgp.audittrail.dao.AuditTrailDbsMgr;
import org.osgp.audittrail.dao.cassandra.AuditTrailCassandraClient;
import org.osgp.audittrail.dao.cassandra.AuditTrailCassandraImpl;
import org.osgp.shared.dbs.Database;
import org.osgp.util.SystemPropertyHelper;
import org.osgp.util.dao.PK;

import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class TestCassandra {

	Cluster cluster;
	Session session;

	private static AuditTrailCassandraClient client = new AuditTrailCassandraClient();
	private static AuditTrailDao dao;

	@BeforeClass
	public static void beforeOnce() {
		SystemPropertyHelper.setupDatabase(Database.CASSANDRA);
		final String ipAddress = "localhost";
		final int port = 9042;
		client.initialize(ipAddress, port);
		AuditTrailDbsMgr.INSTANCE.open();
		// AuditTrailDaoFact.INSTANCE.reset();
		dao = new AuditTrailCassandraImpl();
	}

	@AfterClass
	public static void afterOnce() {
		client.deleteKeyspace();
		client.close();
	}

	@Test
	public void test() {
		final String correlId = UUID.randomUUID().toString();
		RequestResponseMsg reqRespMsg = RequestResponseMsg.newBuilder().setCorrelId(correlId).build();
		dao.saveRequestResponseMsg(reqRespMsg);

		RequestResponseMsg reqRespMsg2 = dao.getRequestResponseMsg(correlId);
		Assert.assertEquals(correlId, reqRespMsg2.getCorrelId());

		RequestResponseMsg reqRespMsg3 = RequestResponseMsg.newBuilder(reqRespMsg2)
				.setCommon(CommonMsg.newBuilder().setDeviceId("DEV001").build()).build();
		dao.saveRequestResponseMsg(reqRespMsg3);

		List<PK> pks = dao.getAllRequestResponseMsgPKs();
		RequestResponseMsg reqRespMsg4 = dao.getRequestResponseMsg(correlId);
		Assert.assertEquals("DEV001", reqRespMsg4.getCommon().getDeviceId());

		dao.delete(pks.get(0));
	}

}
