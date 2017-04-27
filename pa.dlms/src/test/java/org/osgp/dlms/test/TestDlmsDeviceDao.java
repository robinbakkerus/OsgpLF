package org.osgp.dlms.test;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.osgp.dlms.DC;
import org.osgp.pa.dlms.application.dao.DlmsDao;
import org.osgp.pa.dlms.application.dao.DlmsDaoFact;
import org.osgp.pa.dlms.application.dao.DlmsDbsMgr;
import org.osgp.pa.dlms.application.dao.cassandra.DlmsCassandraClient;
import org.osgp.pa.dlms.util.DlmsDeviceMsgBuildHelper;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.Database;
import org.osgp.shared.exceptionhandling.FunctionalException;
import org.osgp.util.SystemPropertyHelper;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.redis.RedisUtils;

import com.aerospike.client.Key;
import com.alliander.osgp.dlms.DlmsDeviceMsg;

public class TestDlmsDeviceDao {

	private static final String DEVID = "DEV" + new Date().getTime();

	@After
	public void afterEach() {
		DlmsDbsMgr.INSTANCE.close();
		SystemPropertyHelper.clearProperties();
	}

	@Test
	public void testCassandraDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.CASSANDRA);
		testDao(new PK(DEVID));
	}

//	@Test
	public void testPerstDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.PERST);
		String key = CC.RK_DLMS_DEVICE + DEVID;
		testDao(new PK(key));
	}

//	@Test
	public void testRedisDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.Redis);
		byte[] key = RedisUtils.key(CC.RK_DLMS_DEVICE, DEVID);
		testDao(new PK(key));
	}

//	@Test
	public void testAerospikeDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.Aerospike);
		final Key key = new Key(DC.DBS_NAMESPACE_DLMS, DC.TABLE_DLMS_DEVICE, DEVID);
		testDao(new PK(key));
	}

	private void testDao(PK pk) {
		DlmsDbsMgr.INSTANCE.open();
		DlmsDaoFact.INSTANCE.reset();

		try {
		DlmsDeviceMsg dlmsDeviceMsg1 = DlmsDeviceMsgBuildHelper.makeDlmsDeviceMsg(DEVID);
		DlmsDeviceMsg dlmsDeviceMsg2 = dao().save(dlmsDeviceMsg1);
		Assert.assertTrue(dlmsDeviceMsg1.getIdentification().equals(dlmsDeviceMsg1.getIdentification()));
		DlmsDeviceMsg dlmsDeviceMsg3 = DlmsDeviceMsg.newBuilder(dlmsDeviceMsg2)
				.setChallLen(10).build();
		DlmsDeviceMsg dlmsDeviceMsg4 = dao().save(dlmsDeviceMsg3);
		Assert.assertTrue(dlmsDeviceMsg3.getChallLen() == dlmsDeviceMsg4.getChallLen());
		
		DlmsDeviceMsg dlmsDeviceMsg5 = dao().findByDeviceId(DEVID);
		Assert.assertTrue(dlmsDeviceMsg5.getIdentification().equals(dlmsDeviceMsg4.getIdentification()));
		Assert.assertTrue(dlmsDeviceMsg5.getChallLen() == dlmsDeviceMsg4.getChallLen());
		} catch (FunctionalException e) {
			Assert.fail("error " + e);
		}
	}

	private DlmsDao dao() {
		return DlmsDaoFact.INSTANCE.getDao();
	}
}
