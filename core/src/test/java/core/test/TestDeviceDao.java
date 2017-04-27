package core.test;

import static org.osgp.core.dbs.CoreCassandraClient.TABLE_CORE_DEVICE;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.osgp.core.dbs.CoreDao;
import org.osgp.core.dbs.CoreDaoFact;
import org.osgp.core.dbs.CoreDbsMgr;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.Database;
import org.osgp.util.SystemPropertyHelper;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.redis.RedisUtils;

import com.aerospike.client.Key;
import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.DeviceMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

public class TestDeviceDao {

	private static final String DEVID = "DEV" + new Date().getTime();
	
	@After
	public void afterEach() {
		CoreDbsMgr.INSTANCE.close();
		SystemPropertyHelper.clearProperties();
	}
	
	@Test
	public void testPerstDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.PERST);
		String key = CC.RK_CORE_DEVICE + DEVID;
		testDao(new PK(key));
	}

	@Test
	public void testRedisDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.Redis);
		byte[] key = RedisUtils.key(CC.RK_CORE_DEVICE, DEVID);
		testDao(new PK(key));
	}

	@Test
	public void testAerospikeDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.Aerospike);
		final Key key = new Key(CC.DBS_NAMESPACE_CORE, TABLE_CORE_DEVICE, DEVID);
		testDao(new PK(key));
	}

	private void testDao(PK pk) {
		CoreDbsMgr.INSTANCE.open();
		CoreDaoFact.INSTANCE.reset();

		DeviceMsg deviceMsg1 = DeviceMsg.newBuilder().setDeviceId(DEVID).build();
		dao().saveCoreDevice(deviceMsg1);
		RequestResponseMsg regRespMsg = RequestResponseMsg.newBuilder().setCommon(
				CommonMsg.newBuilder().setDeviceId(DEVID).build()).build();
		DeviceMsg deviceMsg2 = dao().findDevice(regRespMsg);
		Assert.assertTrue(deviceMsg2.getDeviceId().equals(DEVID));
		DeviceMsg deviceMsg3 = DeviceMsg.newBuilder(deviceMsg2).setGatewayDevice("DEVXXX").build();
		dao().saveCoreDevice(deviceMsg3);
		DeviceMsg deviceMsg4 = dao().getCoreDevice(DEVID);
		Assert.assertTrue(deviceMsg4.getDeviceId().equals(DEVID));
		Assert.assertTrue(deviceMsg4.getGatewayDevice().equals("DEVXXX"));
		dao().delete(pk);
		Object obj = dao().findDevice(regRespMsg);
		Assert.assertTrue(obj == null);
	}

	
	private CoreDao dao() {
		return CoreDaoFact.INSTANCE.getDao();
	}
}
