package org.osgp.pa.dlms.application.dao;

import java.util.List;

import org.osgp.shared.exceptionhandling.FunctionalException;
import org.osgp.util.dao.PK;

import com.alliander.osgp.dlms.DlmsDeviceMsg;

public interface DlmsDao {
	
	DlmsDeviceMsg findByDeviceId(final String deviceId) throws FunctionalException;
	
	DlmsDeviceMsg save(DlmsDeviceMsg dlmsDevice);
	void saveList(List<DlmsDeviceMsg> dlmsDeviceList);
	
	Object get(PK pk);

	void commit();
}
