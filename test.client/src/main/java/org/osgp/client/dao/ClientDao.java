package org.osgp.client.dao;

import java.util.List;

import org.osgp.util.dao.PK;

import com.alliander.osgp.dlms.DlmsDevOperMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

public interface ClientDao {

	void saveDeviceOperation(final DlmsDevOperMsg deviceOperation);
	
	/**
	 * this saves the RequestResponseMsg that can be sent to the platform in the 'bundled-device-operations' table  in sm-int layer.
	 * @param dlmsRegRespMsg
	 * @return
	 */
	String saveBundledDeviceOperation(final RequestResponseMsg dlmsRegRespMsg);

	/**
	 * The retrieves all RequestResponseMsg from 'bundled-device-operation' table in the sm-int layer
	 * @return
	 */
	List<RequestResponseMsg> getAllBundledDeviceOperations();

	List<DeviceOperationTuple> getAllDeviceOperations();

	DlmsDevOperMsg getDeviceOperation(PK pk);
	
	void delete(PK pk);

	Object get(PK pk);
}

