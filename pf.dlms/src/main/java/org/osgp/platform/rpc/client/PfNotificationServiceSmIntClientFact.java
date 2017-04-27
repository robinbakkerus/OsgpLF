package org.osgp.platform.rpc.client;

import java.util.List;

import org.osgp.util.ConfigHelper;
import org.osgp.util.Server;
import org.osgp.util.rpc.AbstractNotificationServiceClient;
import org.osgp.util.rpc.AbstractNotificationServiceClientFact;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class PfNotificationServiceSmIntClientFact extends AbstractNotificationServiceClientFact {

	private static PfNotificationServiceSmIntClientFact sInstance = null;
	
	private static Config config = ConfigFactory.load("platform");
	
	private PfNotificationServiceSmIntClientFact(Class<? extends AbstractNotificationServiceClient> serviceClientClass, List<Server> servers) {
		super(serviceClientClass, servers);
	}
	
	public static PfIntNotificationServiceSmIntClient client() {
		if (sInstance == null) {
			List<Server> servers = ConfigHelper.getServers(config, "grpc.notification-service");
			sInstance = new PfNotificationServiceSmIntClientFact(PfIntNotificationServiceSmIntClient.class, servers);
		}
		return (PfIntNotificationServiceSmIntClient) sInstance.getClient();
	}
}
