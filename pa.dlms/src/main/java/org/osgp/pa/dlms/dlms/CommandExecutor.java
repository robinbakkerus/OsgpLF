package org.osgp.pa.dlms.dlms;

import org.openmuc.jdlms.DlmsConnection;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.shared.ResponseMsg;
import com.alliander.osgp.shared.ResponseValuesMsg;

/**
 * Interface for executing a command on a smart meter over a client connection,
 * taking input of type <T>.
 *
 * @param <T>
 *            the type of object used as input for executing a command.
 * @param <R>
 *            the type of object returned as a result from executing a command.
 */
public interface CommandExecutor {

	ResponseValuesMsg execute(DlmsConnection conn, DlmsDevice device, DlmsActionMsg reqItem) throws ProtocolAdapterException;

}