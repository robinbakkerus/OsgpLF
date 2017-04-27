package org.osgp.pa.dlms.service;

import java.util.List;

import org.openmuc.jdlms.DlmsConnection;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.shared.exceptionhandling.TechnicalException;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

public interface BundleService {

	List<DlmsActionMsg> callExecutors(final DlmsConnection connection, 
			final DlmsDevice device,
			final RequestResponseMsg reqRespMsg) throws ProtocolAdapterException, TechnicalException ;

}
