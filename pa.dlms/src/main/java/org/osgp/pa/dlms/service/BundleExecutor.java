package org.osgp.pa.dlms.service;

import org.osgp.pa.dlms.dlms.CommandExecutor;

import com.alliander.osgp.dlms.DlmsActionMsg;

public interface BundleExecutor {

	CommandExecutor getExecutor(final DlmsActionMsg action) throws ExecutorNotFoundException;
}
