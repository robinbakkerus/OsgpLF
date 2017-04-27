package org.osgp.pa.dlms.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgp.shared.CC;

import com.alliander.osgp.dlms.DlmsDeviceMsg;
import com.alliander.osgp.dlms.SecurityKeyMsg;
import com.alliander.osgp.dlms.SecurityKeyMsg.SecurityKeyType;
import com.alliander.osgp.shared.DeviceMsg;

public class DlmsDeviceMsgBuildHelper {

	private static int sLogicalIndex = 1;
	
	private static final SecurityKeyType[] KEYTYPES = new SecurityKeyType[] { SecurityKeyType.AUTH_KEY,
			SecurityKeyType.ENC_KEY, SecurityKeyType.MASTER_KEY };

	private static final String[] KEYS = new String[] {
			"bc082efed278e1bbebddc0431877d4fae80fa4e72925b6ad0bc67c84b8721598eda8458bcc1b2827fe6e5e7918ce22fd",
			"bc082efed278e1bbebddc0431877d4fa2df7728229f3e03c57b2549142b40d047b35011dbf9f77ad91db5fe6f19a7b9c",
			"bc082efed278e1bbebddc0431877d4fa16374b00e96dd102beab666dcb72efbd1f0b868412497f6d3d0c62caa4700585" };

	public static DlmsDeviceMsg makeDlmsDeviceMsg(final String devid) {
		List<SecurityKeyMsg> seckeys = new ArrayList<>();
		for (int j = 0; j < 3; j++) {
			seckeys.add(makeSecurityKey(devid, j));
		}
		
		return DlmsDeviceMsg.newBuilder().setIdentification(devid).setLogicalId(logicalId())
			.addAllSecurityKeys(seckeys).build();
	}
	
	public static SecurityKeyMsg makeSecurityKey(String devid, int listIndex) {
		SecurityKeyType keytyp = KEYTYPES[listIndex];
		String keyval = KEYS[listIndex];
		SecurityKeyMsg mastKey = SecurityKeyMsg.newBuilder().setCreatedAt(now()).setValidFrom(now()).setKeyType(keytyp)
				.setKey(keyval).build();
		return mastKey;
	}
	
	public static DeviceMsg makeDeviceMsg(final String devid) {
    	return DeviceMsg.newBuilder()
    		.setDeviceId(devid)
    		.setActivated(Boolean.TRUE)
    		.setLat(1234567).setLat(56789)
    		.setNetworkAddress("localhost")
    		.setOrganisations(CC.INFOSTROOM)
    		.setProtocol("GPRS").build();		
    }
	    
	private static int logicalId() {
		if (sLogicalIndex > 1000) {
			sLogicalIndex = 1;
		}
		return sLogicalIndex++;
	}

	private static long now() {
		return new Date().getTime();
	}


}
