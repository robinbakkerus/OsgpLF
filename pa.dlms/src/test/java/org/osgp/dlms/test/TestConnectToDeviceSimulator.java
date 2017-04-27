package org.osgp.dlms.test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgp.pa.dlms.application.dao.DlmsDbsMgr;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.pa.dlms.device.Hls5ConnectorLite;
import org.osgp.shared.exceptionhandling.FunctionalException;
import org.osgp.shared.exceptionhandling.TechnicalException;

import com.alliander.osgp.shared.DeviceMsg;

public class TestConnectToDeviceSimulator {

	
	@BeforeClass
	public static void beforeOnce() {
		DlmsDbsMgr.INSTANCE.open();
	}
	
	@Test
	public void test() {
		try {
			DeviceMsg devicemsg = DeviceMsg.newBuilder().setDeviceId("DEV1")
					.setNetworkAddress("localhost").build();
			DlmsDevice dlmsDevice = DlmsDevice.retrieve(devicemsg);
			final Hls5ConnectorLite connector = new Hls5ConnectorLite(dlmsDevice);
			connector.connect();
		} catch (FunctionalException | TechnicalException e) {
			Assert.fail(e.getMessage());
		}
	}

}
