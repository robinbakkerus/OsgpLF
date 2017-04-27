package org.osgp.core.rpc;

import org.osgp.util.Server;
import org.osgp.util.rpc.AbstractOsgpServiceClient;

import com.alliander.osgp.shared.RequestResponseMsg;

/**
 * This is gRpc client for protocol adapter
 * 
 * @author robin
 *
 */
public class OsgpServicePFClient extends AbstractOsgpServiceClient {

	public OsgpServicePFClient(final Server server) {
		super(server);
	}
	
	public void addResponse(final RequestResponseMsg reqRespMsg) {
		addNextRequest(reqRespMsg);
	}
	
}
