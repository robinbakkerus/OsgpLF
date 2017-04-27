package org.osgp.client.test;

import java.util.Date;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgp.client.dao.ClientDbsMgr;
import org.osgp.core.dbs.CoreDao;
import org.osgp.core.dbs.CoreDaoFact;
import org.osgp.core.dbs.CoreDbsMgr;
import org.osgp.pa.dlms.application.dao.DlmsDbsMgr;
import org.osgp.shared.CC;
import org.osgp.util.dao.PK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.DeviceMsg;

public class TestReadWriteDevice implements CC {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestReadWriteDevice.class.getName());

	@BeforeClass
	public static void beforeOnce() {
		ClientDbsMgr.INSTANCE.open();
		CoreDbsMgr.INSTANCE.open();
		DlmsDbsMgr.INSTANCE.open();
	}

	@AfterClass
	public static void afterOnce() {
		ClientDbsMgr.INSTANCE.close();
		CoreDbsMgr.INSTANCE.close();
		DlmsDbsMgr.INSTANCE.close();
	}

	private static final int MAX = 10;

	@Test
	public void test() {
		testInsertManyDevices();
//		testLookupDevices();
	}

	private void testInsertManyDevices() {
		LOGGER.info("start inserting devices every tick = 50k");
		long startedAt = now();

		for (int i = 1; i < MAX; i++) {
			String devid = "DEV" + i;
			DeviceMsg device = DeviceMsg.newBuilder().setDeviceId(devid).setActivated(Boolean.TRUE).setLat(1234567)
					.setLat(56789).setNetworkAddress(ipAddress()).setProtocol("GPRS").build();
			coreDao().saveCoreDevice(device);
			if (i % 50000 == 0)
				System.out.print(".");
		}
		System.out.println();
		LOGGER.warn("inserted " + MAX + " records in " + (now() - startedAt) + " msecs");
	}

//	private void testLookupDevices() {
//		long startedAt = now();
//		for (int i = 1; i < MAX; i++) {
//			String deviceId = "DEV" + i;
//			Object object = coreDao().get(new PK(RK_CORE_DEVICE + deviceId));
//			Assert.assertTrue(object != null);
//		}
//		LOGGER.warn("Lookup of " + (MAX / 100) + " devices took: " + (now() - startedAt) + " msecs");
//	}

	private long now() {
		return new Date().getTime();
	}

	long startedAt = now();

	private String ipAddress() {
		Random rand = new Random();
		return "" + (rand.nextInt(126) + 1) + "." + (rand.nextInt(126) + 1) + "." + (rand.nextInt(126) + 1) + "."
				+ (rand.nextInt(126) + 1);
	}

	private CoreDao coreDao() {
		return CoreDaoFact.INSTANCE.getDao();
	}

}
