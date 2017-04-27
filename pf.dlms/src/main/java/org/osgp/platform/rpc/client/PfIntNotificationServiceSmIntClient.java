package org.osgp.platform.rpc.client;

import org.osgp.util.Server;
import org.osgp.util.rpc.AbstractNotificationServiceClient;

/**
 * This is gRpc client for protocol adapter
 * 
 * @author robin
 *
 */
public class PfIntNotificationServiceSmIntClient extends AbstractNotificationServiceClient {

	public PfIntNotificationServiceSmIntClient(final Server server) {
		super(server);
	}
}
