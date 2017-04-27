package org.osgp.platform.test;

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
import org.osgp.shared.dbs.UndeliveredTuple;
import org.osgp.util.SystemPropertyHelper;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.redis.RedisUtils;

import com.aerospike.client.Key;
import com.alliander.osgp.shared.RequestResponseMsg;

public class TestUndeliveredDao {

	private static final String CORRELID = UUID.randomUUID().toString();

	@After
	public void afterEach() {
		PlatformDbsMgr.INSTANCE.close();
		SystemPropertyHelper.clearProperties();
	}

	@Test
	public void testPerstDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.PERST);
		String key = CC.RK_PF2CORE_UNDELIVERED + CORRELID;
		testDao(new PK(key));
	}

	@Test
	public void testRedisDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.Redis);
		byte[] key = RedisUtils.key(CC.RK_PF2CORE_UNDELIVERED, CORRELID);
		testDao(new PK(key));
	}

	@Test
	public void testAerospikeDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.Aerospike);
		final Key key = new Key(DC.DBS_NAMESPACE_PLATFORM, DC.TABLE_UNDELIVERED_CORE, CORRELID);
		testDao(new PK(key));
	}

	private void testDao(PK pk) {
		PlatformDbsMgr.INSTANCE.open();
		PlatformDaoFact.INSTANCE.reset();

		RequestResponseMsg reqRespMsg1 = RequestResponseMsg.newBuilder().setCorrelId(CORRELID).build();
		dao().saveUndeliveredRequest(reqRespMsg1);
		List<UndeliveredTuple> allUndelivered = dao().getAllUndeliveredRequests();
		Assert.assertTrue(allUndelivered.stream().anyMatch(t -> t.getPk().equals(pk)));

		dao().delete(PlatformTable.CORE_UNDELIVERED, pk);
		allUndelivered = dao().getAllUndeliveredRequests();
		Assert.assertFalse(allUndelivered.stream().anyMatch(t -> t.getPk().equals(pk)));
	}

	private PlatformDao dao() {
		return PlatformDaoFact.INSTANCE.getDao();
	}
}
