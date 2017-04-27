package org.osgp.pa.dlms.application.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.osgp.pa.dlms.application.dao.DlmsDao;
import org.osgp.pa.dlms.application.dao.DlmsDaoFact;
import org.osgp.shared.exceptionhandling.FunctionalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DlmsDeviceMsg;
import com.alliander.osgp.dlms.SecurityKeyMsg;
import com.alliander.osgp.dlms.SecurityKeyMsg.SecurityKeyType;
import com.alliander.osgp.shared.DeviceMsg;
import com.google.protobuf.Descriptors;

/**
 * wrapper classes for DlmsDeviceMsg and SecurityKey
 * via a static method a DlmsDevice is created from the corresponding database records.
 *
 */
public class DlmsDevice {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlmsDevice.class.getName());

	private DeviceMsg deviceMsg;

	private DlmsDeviceMsg dlmsDeviceMsg;
	
	private Map<SecurityKeyType, SecurityKeyMsg> validSecurityKeys;
	
	public static DlmsDevice retrieve(final DeviceMsg aDeviceMsg) throws FunctionalException {
		DlmsDevice result = new DlmsDevice();
		result.deviceMsg = aDeviceMsg;
		result.dlmsDeviceMsg = dlmsDeviceDao().findByDeviceId(aDeviceMsg.getDeviceId());
		return result;
	}
	
	private DlmsDevice() {
		super();
	}
	
	
	public DlmsDevice(DeviceMsg deviceMsg, DlmsDeviceMsg dlmsDeviceMsg) {
		super();
		this.dlmsDeviceMsg = dlmsDeviceMsg;
		this.deviceMsg = deviceMsg;
	}

	public String getIdentification() {
		return this.dlmsDeviceMsg.getIdentification();
	}

	public String getNetworkAddress() {
		return this.deviceMsg.getNetworkAddress();
	}
	
	public boolean hasNewSecurityKey() {
		return false; //TODO
	}
	
	public void discardInvalidKeys() {
		//TODO
	}
	
	public boolean isWithListSupported() {
		return false; //TODO
	}
	
	public SecurityKeyMsg getValidSecurityKey(final SecurityKeyType securityKeyType) {
		SecurityKeyMsg result = findValidSecurityKey(securityKeyType);
		if (result == null) {
			LOGGER.error("no valid securitykey found for " + getIdentification() + " " + securityKeyType);
		} 
		
		return result;
	}
	
	public String getAuthKey() {
		return getKey(SecurityKeyType.AUTH_KEY);
	}
	
	public String getEncKey() {
		return getKey(SecurityKeyType.ENC_KEY);
	}

	public String getMasterKey() {
		return getKey(SecurityKeyType.MASTER_KEY);
	}
	
	private String getKey(SecurityKeyType keytype) {
		SecurityKeyMsg seckey = getValidSecurityKey(keytype);
		if (seckey != null) {
			return seckey.getKey();
		} else {
			return null;
		}
	}

	public SecurityKeyMsg getNewSecurityKey(final SecurityKeyType securityKeyType) {
		SecurityKeyMsg result = SecurityKeyMsg.newBuilder().build();
		return result;
	}
	
	public void promoteInvalidKey() {
		//TODO
	}
	
	public DlmsDevice save() {
		return this;
	}
	
	public int getLogicalDeviceId() {
		return this.dlmsDeviceMsg.getLogicalId();
	}
	
	// TODO
	public boolean isSelectiveAccessSupported() {
		return true;
	}
	//------------------------------------------
	
	private SecurityKeyMsg findValidSecurityKey(final SecurityKeyType keyType) {
		if (validSecurityKeys == null) {
			validSecurityKeys = fillSecurityKeys();
		}
		
		if (validSecurityKeys.containsKey(keyType)) {
			return validSecurityKeys.get(keyType);
		} else {
			return null;
		}
	}

	private Map<SecurityKeyType, SecurityKeyMsg>  fillSecurityKeys() {
		validSecurityKeys = new HashMap<>();

		for (int j=0; j <  this.dlmsDeviceMsg.getRepeatedFieldCount(securityKeys()); j++) {
			SecurityKeyMsg seckey = (SecurityKeyMsg) this.dlmsDeviceMsg.getRepeatedField(securityKeys(), j);
			SecurityKeyType keyType = seckey.getKeyType();
			long validFom = seckey.getValidFrom();
			long validTo = seckey.getValidTo();
			if (validTo == 0 && now() > validFom) {
				validSecurityKeys.put(keyType, seckey); //todo wat als er al een bestaat?
			}
		}
		
		return validSecurityKeys;
	}
	

	private static DlmsDao dlmsDeviceDao() {
		return DlmsDaoFact.INSTANCE.getDao();
	}

	private Descriptors.FieldDescriptor securityKeys() {
		return DlmsDeviceMsg.getDescriptor().findFieldByName("securityKeys");
	}
	
	private long now() {
		return new Date().getTime();
	}
	//------ getters&setters
	
	public DlmsDeviceMsg getDlmsDeviceMsg() {
		return dlmsDeviceMsg;
	}


	public DeviceMsg getDeviceMsg() {
		return deviceMsg;
	}

	public void setDeviceMsg(DeviceMsg deviceMsg) {
		this.deviceMsg = deviceMsg;
	}

	
}
