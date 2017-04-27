package org.osgp.dlms.test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.shared.dbs.AerospikeServer;
import org.osgp.shared.exceptionhandling.FunctionalException;

import com.alliander.osgp.dlms.SecurityKeyMsg;
import com.alliander.osgp.dlms.SecurityKeyMsg.SecurityKeyType;
import com.alliander.osgp.shared.DeviceMsg;

public class TestDlmsDevice {

	private static AerospikeServer aerospikeServer;
	
	@BeforeClass
	public static void beforeOnce() {
		aerospikeServer = new AerospikeServer("localhost", 3000);
		System.out.println("started " + aerospikeServer);
	}
	
	@Test
	public void test() {
		try {
			DeviceMsg devicemsg = DeviceMsg.newBuilder().setDeviceId("DEV1").build();
			DlmsDevice dlmsDevice = DlmsDevice.retrieve(devicemsg);
			Assert.assertTrue(dlmsDevice.getIdentification().equals("DEV1"));
			SecurityKeyMsg mastkey = dlmsDevice.getValidSecurityKey(SecurityKeyType.MASTER_KEY);
			Assert.assertNotNull(mastkey);
		} catch (FunctionalException e) {
			Assert.fail(e.getMessage());
		}
	}

}
