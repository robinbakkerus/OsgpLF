package org.osgp.core.rpc;

import org.osgp.util.Server;
import org.osgp.util.rpc.AbstractOsgpServiceClient;

/**
 * This is gRpc client for protocol adapter
 * 
 * @author robin
 *
 */
public class OsgpServicePAClient extends AbstractOsgpServiceClient {

	public OsgpServicePAClient(final Server server) {
		super(server);
	}
}
