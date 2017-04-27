package org.osgp.smint.dao;

import org.osgp.util.dao.PK;

import com.alliander.osgp.dlms.DlmsDevOperMsg;

public class DeviceOperationTuple {

	private PK pk;
	private DlmsDevOperMsg deviceOperation;
	
	public DeviceOperationTuple(PK pk, DlmsDevOperMsg deviceOperation) {
		super();
		this.pk = pk;
		this.deviceOperation = deviceOperation;
	}

	public PK getPk() {
		return pk;
	}

	public DlmsDevOperMsg getDeviceOperation() {
		return deviceOperation;
	}
	
	
	

}
