package org.osgp.pa.dlms.dlms.cmdexec;

import org.openmuc.jdlms.DlmsConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.pa.dlms.dlms.CommandExecutor;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.shared.ResponseValuesMsg;

public abstract class AbstractCommandExecutor implements CommandExecutor  {

    protected DlmsHelperService dlmsHelperService = new DlmsHelperService();

	@Override
	public ResponseValuesMsg execute(DlmsConnection conn, DlmsDevice device, DlmsActionMsg reqItem)
			throws ProtocolAdapterException {
		return makeResponse(reqItem);
	}
	
	protected ResponseValuesMsg makeResponse(final DlmsActionMsg reqItem) {
		return ResponseValuesMsg.newBuilder().build();
	}


}
