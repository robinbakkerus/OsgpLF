package org.osgp.dlms.test;

import java.util.Date;
import java.util.List;

import org.openmuc.jdlms.DlmsConnection;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.pa.dlms.device.Hls5ConnectorLite;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.pa.dlms.service.BundleService;
import org.osgp.pa.dlms.service.BundleServiceImpl;
import org.osgp.shared.exceptionhandling.TechnicalException;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.DlmsDeviceMsg;
import com.alliander.osgp.dlms.DlmsSpecificMsg;
import com.alliander.osgp.dlms.EmptyMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.dlms.SecurityKeyMsg;
import com.alliander.osgp.dlms.SecurityKeyMsg.SecurityKeyType;
import com.alliander.osgp.shared.ActionMsg;
import com.alliander.osgp.shared.DeviceMsg;
import com.alliander.osgp.shared.ProtocolSpecificMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.Descriptors;

public class TestClientDeviceServer {

	private static final String DEVICE_ID = "EXXXX001692675614";
//	private static final String IP_ADDR = "89.200.96.233";
	private static final String LOCALHOST = "localhost";

	private BundleService service = new BundleServiceImpl();

	public static void main(String args[]) {
		TestClientDeviceServer mainObj = new TestClientDeviceServer();
		mainObj.doTest();
	}

	public void doTest() {
		DlmsDevice dlmsDdevice = makeDevice();
		Hls5ConnectorLite hls5connector = new Hls5ConnectorLite(dlmsDdevice);
		try {
			DlmsConnection conn = hls5connector.connect();
			RequestResponseMsg dlmsReq = makeDlmsReqRespMsg();
			List<DlmsActionMsg> updatedActions;
			updatedActions = service.callExecutors(conn, makeDevice(), dlmsReq);
			System.out.println("succesfully connected, response " + updatedActions);
		} catch (ProtocolAdapterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TechnicalException e) {
			System.err.println(e);
		}
	}
	
	private DlmsDevice makeDevice() {
		DeviceMsg deviceMsg = DeviceMsg.newBuilder().setDeviceId(DEVICE_ID).setNetworkAddress(LOCALHOST).build();
		DlmsDeviceMsg dlmsDeviceMsg = DlmsDeviceMsg.newBuilder().setLogicalId(1).build();
		
		for (int j=0;j<3;j++) {
			SecurityKeyMsg seckey = makeSecurityKey(DEVICE_ID, j);
			dlmsDeviceMsg = DlmsDeviceMsg.newBuilder(dlmsDeviceMsg).
					addRepeatedField(securityKeys(), seckey).build();
		}
		
		return new DlmsDevice(deviceMsg, dlmsDeviceMsg);
	}

	private static final SecurityKeyType[] KEYTYPES = new SecurityKeyType[] { SecurityKeyType.AUTH_KEY,
			SecurityKeyType.ENC_KEY, SecurityKeyType.MASTER_KEY };

	private static final String[] KEYS = new String[] {
			"bc082efed278e1bbebddc0431877d4fae80fa4e72925b6ad0bc67c84b8721598eda8458bcc1b2827fe6e5e7918ce22fd",
			"bc082efed278e1bbebddc0431877d4fa2df7728229f3e03c57b2549142b40d047b35011dbf9f77ad91db5fe6f19a7b9c",
			"bc082efed278e1bbebddc0431877d4fa16374b00e96dd102beab666dcb72efbd1f0b868412497f6d3d0c62caa4700585" };

	private SecurityKeyMsg makeSecurityKey(String devid, int listIndex) {
		SecurityKeyType keytyp = KEYTYPES[listIndex];
		String keyval = KEYS[listIndex];
		SecurityKeyMsg mastKey = SecurityKeyMsg.newBuilder().setCreatedAt(now())
				.setValidFrom(now()).setKeyType(keytyp).setKey(keyval).build();
		return mastKey;
	}
	
	private Descriptors.FieldDescriptor securityKeys() {
		return DlmsDeviceMsg.getDescriptor().findFieldByName("securityKeys");
	}
	
	private long now() {
		return new Date().getTime();
	}
	
	private RequestResponseMsg makeDlmsReqRespMsg() {

		return RequestResponseMsg.newBuilder()
				.setAction(makeAction())
				.setDevice(makeDeviceMsg())
				.build();
	}
	
	private DeviceMsg makeDeviceMsg() {
		DeviceMsg r = DeviceMsg.newBuilder().build();
		return r;
	}
	
	private ActionMsg makeAction() {
		return ActionMsg.newBuilder()
				.setProtocolSpecific(makeSpecific())
				.build();
	}
	
	private ProtocolSpecificMsg makeSpecific() {
		DlmsActionMsg actionMsg = DlmsActionMsg.newBuilder()
				.setGetActualMeterReadMsg(EmptyMsg.newBuilder().build())
				.setRequestType(RequestType.GET_ACTUAL_METER_READS)
				.build();

		DlmsSpecificMsg specific = DlmsSpecificMsg.newBuilder()
				.addActions(actionMsg)
				.build();

		ProtocolSpecificMsg r = ProtocolSpecificMsg.newBuilder()
				.setRaw(specific.toByteString())
				.build();
		
		return r;
	}
	

}
