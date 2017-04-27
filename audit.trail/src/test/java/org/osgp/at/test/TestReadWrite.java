package org.osgp.at.test;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.osgp.audittrail.dao.AuditTrailDao;
import org.osgp.audittrail.dao.AuditTrailDaoFact;
import org.osgp.audittrail.dao.AuditTrailDbsMgr;
import org.osgp.shared.dbs.Database;
import org.osgp.util.SystemPropertyHelper;
import org.osgp.util.dao.PK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.ResponseMsg;
import com.alliander.osgp.shared.ResponseStatus;

public class TestReadWrite {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestReadWrite.class.getName());

	
	private static final int MAX = 10000;

	@After
	public void after() {
		System.out.println("after");
		AuditTrailDbsMgr.INSTANCE.close();
	}
	
	//@Test
	public void testPerformanceAeroSpike() {
		testPerformance(Database.Aerospike);
	}

	//@Test
	public void testPerformanceRedis() {
		testPerformance(Database.Redis);
	}
	
	@Test
	public void testPerformancePerst() {
		testPerformance(Database.PERST);
	}
	
	private void testPerformance(Database dbtype) {
		setupDatabase(dbtype);
		testManyReqeustResponseMsg();
		long seconds = NANOSECONDS.toSeconds(System.nanoTime() - startedAt);
		System.out.println(MAX + " inserts took " + seconds + " secs for " + dbtype);
	}

	private void setupDatabase(Database dbtype) {
		SystemPropertyHelper.setupDatabase(dbtype);
		AuditTrailDbsMgr.INSTANCE.open();
		AuditTrailDaoFact.INSTANCE.reset();
	}
	
	private void testManyReqeustResponseMsg() {
		LOGGER.info("start inserting devices every tick = 50k");
		long startedAt = now();

		for (int i = 1; i < MAX; i++) {
			final String correlId = UUID.randomUUID().toString();
			RequestResponseMsg reqRespMsg = RequestResponseMsg.newBuilder()
					.setCorrelId(correlId)
					.setResponse(ResponseMsg.newBuilder().setStatus(ResponseStatus.NOT_OK).build()).build();

			dao().saveRequestResponseMsg(reqRespMsg);
			
			if (i % 50000 == 0)
				System.out.print(".");
		}
		System.out.println();
		LOGGER.info("inserted " + MAX + " records in " + (now() - startedAt) + " msecs");
	}

	//@Test
	public void testReadWriteAerospike() {
		testReadWrite(Database.Aerospike);
	}
	
	//@Test
	public void testReadWriteRedis() {
		testReadWrite(Database.Redis);
	}
	
	@Test
	public void testReadWritePerst() {
		testReadWrite(Database.PERST);
	}
	
	private void testReadWrite(Database dbtype) {
		setupDatabase(dbtype);
		final String correlId = UUID.randomUUID().toString();
		RequestResponseMsg reqRespMsg1 = RequestResponseMsg.newBuilder()
				.setCorrelId(correlId)
				.setCommon(CommonMsg.newBuilder().setDeviceId("DEV1234").build())
				.setResponse(ResponseMsg.newBuilder().setStatus(ResponseStatus.NOT_OK).build())
				.build();
		dao().saveRequestResponseMsg(reqRespMsg1);
		
		RequestResponseMsg reqRespMsg2 = dao().getRequestResponseMsg(correlId);
		Assert.assertEquals(reqRespMsg1.getCommon().getDeviceId(), reqRespMsg2.getCommon().getDeviceId());
		
		RequestResponseMsg reqRespMsg3 = RequestResponseMsg.newBuilder(reqRespMsg2)
				.setResponse(ResponseMsg.newBuilder().setStatus(ResponseStatus.OK).build())
				.build();
		dao().saveRequestResponseMsg(reqRespMsg3);
		
		RequestResponseMsg reqRespMsg4 = dao().getRequestResponseMsg(correlId);
		Assert.assertEquals(reqRespMsg1.getCommon().getDeviceId(), reqRespMsg4.getCommon().getDeviceId());
		Assert.assertEquals(ResponseStatus.OK, reqRespMsg4.getResponse().getStatus());

	}

	@Test
	public void testSelectAllPerst() {
		testSelectAll(Database.PERST);
	}
	
	private void testSelectAll(Database dbtype) {
		setupDatabase(dbtype);
		List<PK> allPks = dao().getAllRequestResponseMsgPKs();
		Assert.assertTrue(allPks.size() > 0);
	}
	
	private long now() {
		return new Date().getTime();
	}

	long startedAt = System.nanoTime();

	private AuditTrailDao dao() {
		return AuditTrailDaoFact.INSTANCE.getDao();
	}

}
