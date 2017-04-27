package core.test;

import static org.osgp.core.dbs.CoreCassandraClient.TABLE_CORE_SCHEDULE;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.osgp.core.dbs.CoreDao;
import org.osgp.core.dbs.CoreDaoFact;
import org.osgp.core.dbs.CoreDbsMgr;
import org.osgp.core.dbs.ScheduledTaskTuple;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.Database;
import org.osgp.util.SystemPropertyHelper;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.redis.RedisUtils;

import com.aerospike.client.Key;
import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

public class TestScheduledRequestDao {

	private static final String CORRELID = UUID.randomUUID().toString();
	
	@After
	public void afterEach() {
		CoreDbsMgr.INSTANCE.close();
		SystemPropertyHelper.clearProperties();
	}
	
	@Test
	public void testPerstDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.PERST);
		String key = CC.RK_CORE_SCHEDTASKS + CORRELID;
		testDao(new PK(key));
	}

	@Test
	public void testRedisDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.Redis);
		byte[] key = RedisUtils.key(CC.RK_CORE_SCHEDTASKS, CORRELID);
		testDao(new PK(key));
	}
	
	@Test
	public void testCassandraDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.CASSANDRA);
		testDao(new PK(CORRELID, TABLE_CORE_SCHEDULE));
	}


	@Test
	public void testAerospikeDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.Aerospike);
		final Key key = new Key(CC.DBS_NAMESPACE_CORE, TABLE_CORE_SCHEDULE, CC.PREFIX_SCHEDULE + CORRELID);
		testDao(new PK(key));
	}

	private void testDao(PK pk) {
		CoreDbsMgr.INSTANCE.open();
		CoreDaoFact.INSTANCE.reset();

		RequestResponseMsg reqRespMsg1 = RequestResponseMsg.newBuilder().setCorrelId(CORRELID).build();
		dao().saveReqRespToScheduleRepo(reqRespMsg1);
		RequestResponseMsg reqRespMsg2 = dao().findScheduledReqRespMsg(CORRELID);
		Assert.assertTrue(reqRespMsg2.getCorrelId().equals(CORRELID));
		RequestResponseMsg reqRespMsg3 = RequestResponseMsg.newBuilder(reqRespMsg2)
				.setCommon(CommonMsg.newBuilder().setApplicationName("A").build()).build();
		dao().saveReqRespToScheduleRepo(reqRespMsg3);
		RequestResponseMsg reqRespMsg4 = dao().findScheduledReqRespMsg(CORRELID);
		Assert.assertTrue(reqRespMsg4.getCommon().getApplicationName().equals("A"));
		
		List<PK> allPks = dao().getAllScheduledTaskPks();
		Assert.assertTrue(allPks.stream().anyMatch(p -> p.equals(pk)));
		List<ScheduledTaskTuple> schedTasks = dao().getAllScheduledTasks();
		Assert.assertTrue(schedTasks.stream().anyMatch(t -> t.getPk().equals(pk)));
		
		dao().delete(pk);
		Object obj = dao().findScheduledReqRespMsg(CORRELID);
		Assert.assertTrue(obj == null);
	}

	
	private CoreDao dao() {
		return CoreDaoFact.INSTANCE.getDao();
	}
}
