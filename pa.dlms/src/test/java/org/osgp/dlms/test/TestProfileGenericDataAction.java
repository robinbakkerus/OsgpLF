package org.osgp.dlms.test;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openmuc.jdlms.DlmsConnection;
import org.osgp.pa.dlms.application.dao.DlmsDbsMgr;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.pa.dlms.device.Hls5ConnectorLite;
import org.osgp.pa.dlms.dlms.cmdexec.GetProfileGenericDataCmdExec;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.shared.exceptionhandling.FunctionalException;
import org.osgp.shared.exceptionhandling.TechnicalException;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.ProfileGenericDataMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.shared.DeviceMsg;

public class TestProfileGenericDataAction {

	
	@BeforeClass
	public static void beforeOnce() {
		DlmsDbsMgr.INSTANCE.open();
	}
	

	@Test
	public void test() {
		try {
		GetProfileGenericDataCmdExec cmdexec = new GetProfileGenericDataCmdExec();
		DlmsDevice dlmsDevice = dlmsDevice();
		cmdexec.execute(getConnection(dlmsDevice), dlmsDevice, request());
		} catch (FunctionalException | TechnicalException | ProtocolAdapterException e) {
			Assert.fail(e.getMessage());
		}
	}	

	private DlmsConnection getConnection(DlmsDevice dlmsDevice) throws TechnicalException {
			final Hls5ConnectorLite connector = new Hls5ConnectorLite(dlmsDevice);
			return connector.connect();
	}


	private DlmsDevice dlmsDevice() throws FunctionalException {
		DeviceMsg devicemsg = DeviceMsg.newBuilder().setDeviceId("DEV1")
				.setNetworkAddress("localhost").build();
		DlmsDevice dlmsDevice = DlmsDevice.retrieve(devicemsg);
		return dlmsDevice;
	}
	
	@Test
	public void testKenter() {
		try {
		GetProfileGenericDataCmdExec cmdexec = new GetProfileGenericDataCmdExec();
		DlmsDevice dlmsDevice = dlmsDevice();
		cmdexec.execute(getConnection(dlmsDevice), dlmsDevice, request());
		} catch (FunctionalException | TechnicalException | ProtocolAdapterException e) {
			Assert.fail(e.getMessage());
		}
	}	

	private DlmsActionMsg request() {
		return DlmsActionMsg.newBuilder()
				.setRequestType(RequestType.PROFILE_GENERIC_DATA)
				.setProfileGenericDataMsg(data())
				.build();
	}
	
	private ProfileGenericDataMsg data() {
		final DateTime dtFrom = new DateTime(2107,1,1,0,0);
		final DateTime dtTo = DateTime.now();
		return ProfileGenericDataMsg.newBuilder().setObisCode(obisCode())
				.setDateFrom(dtFrom.toDate().getTime())
				.setDateTo(dtTo.toDate().getTime()).build();
	}
	
//	private ObisCodeMgs obisCode() {
//		return ObisCodeMgs.newBuilder().setA(1).setB(0).setC(99).setD(1).setE(0).setF(255).build();
//	}
	
	private String obisCode() {
		return "1.0.99.1.0.255";
	}
	
//	private DlmsDevice kenterDevice() throws FunctionalException {
//		DeviceMsg devicemsg = DeviceMsg.newBuilder().setDeviceId("KENTER")
//				.setNetworkAddress("localhost").build();
//		DlmsDevice dlmsDevice = DlmsDevice.retrieve(devicemsg);
//		return dlmsDevice;
//	}
//	
}
