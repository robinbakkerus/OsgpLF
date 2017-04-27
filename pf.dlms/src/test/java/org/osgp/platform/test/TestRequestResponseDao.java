package org.osgp.platform.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.osgp.dlms.DC;
import org.osgp.platform.dbs.PlatformDao;
import org.osgp.platform.dbs.PlatformDaoFact;
import org.osgp.platform.dbs.PlatformDbsMgr;
import org.osgp.platform.dbs.PlatformTable;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.Database;
import org.osgp.util.SystemPropertyHelper;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.redis.RedisUtils;

import com.aerospike.client.Key;
import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.CorrelIdMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;

public class TestRequestResponseDao {

	private static final String CORRELID = UUID.randomUUID().toString();
	
	@After
	public void afterEach() {
		PlatformDbsMgr.INSTANCE.close();
		SystemPropertyHelper.clearProperties();
	}
	
	@Test
	public void testPerstDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.PERST);
		String key = CORRELID; //If redis CC.RK_PF_REQUEST_RESP + CORRELID;
		testDao(new PK(key));
	}

//	@Test
	public void testRedisDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.Redis);
		byte[] key = RedisUtils.key(CC.RK_PF_REQUEST_RESP, CORRELID);
		testDao(new PK(key));
	}

//	@Test
	public void testAerospikeDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.Aerospike);
		final Key key = new Key(DC.DBS_NAMESPACE_PLATFORM, DC.TABLE_REQRESP_MSG, CORRELID);
		testDao(new PK(key));
	}

	@Test
	public void testCassandraDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.CASSANDRA);
		String key = CORRELID;
		testDao(new PK(key));
	}

	private void testDao(PK pk) {
		PlatformDbsMgr.INSTANCE.open();
		PlatformDaoFact.INSTANCE.reset();

		try {
			RequestResponseMsg reqRespMsg1 = RequestResponseMsg.newBuilder().setCorrelId(CORRELID).build();
			dao().saveRequestResponse(reqRespMsg1);
			RequestResponseMsg reqRespMsg2 = dao().getResponse(CORRELID);
			Assert.assertTrue(reqRespMsg2.getCorrelId().equals(CORRELID));
			RequestResponseMsg reqRespMsg3 = RequestResponseMsg.newBuilder(reqRespMsg2)
					.setCommon(CommonMsg.newBuilder().setApplicationName("A").build()).build();
			dao().saveRequestResponse(reqRespMsg3);
			RequestResponseMsg reqRespMsg4 = dao().getResponse(CorrelIdMsg.newBuilder().setCorrelid(CORRELID).build());
			Assert.assertTrue(reqRespMsg4.getCommon().getApplicationName().equals("A"));
			
			List<PK> allPks = dao().getAllRequestResponseMsgPKs();
			Assert.assertTrue(allPks.stream().anyMatch(p -> p.equals(pk)));
			
			dao().delete(PlatformTable.REQ_RESP, pk);
			Object obj = dao().getResponse(CORRELID);
			Assert.assertTrue(obj == null);
			
			List<RequestResponseMsg> reqrespList = new ArrayList<>();
			for (int i=0; i<10; i++) {
				reqrespList.add(RequestResponseMsg.newBuilder().setCorrelId(UUID.randomUUID().toString()).build());
			}
			dao().saveRequestResponses(reqrespList);
			List<PK> all10Pks = dao().getAllRequestResponseMsgPKs();
			Assert.assertTrue(all10Pks.size() == 10);
			dao().deleteList(PlatformTable.REQ_RESP, all10Pks);
			List<PK> allPksAfterDelete = dao().getAllRequestResponseMsgPKs();
			Assert.assertTrue(allPksAfterDelete.size() == 0);
			
		} catch (InvalidProtocolBufferException e) {
			Assert.fail("error " + e);
		}
	}

	
	private PlatformDao dao() {
		return PlatformDaoFact.INSTANCE.getDao();
	}
}
