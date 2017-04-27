package org.osgp.platform.rpc.client;

import java.util.List;

import org.osgp.util.ConfigHelper;
import org.osgp.util.Server;
import org.osgp.util.rpc.AbstractOsgpServiceClient;
import org.osgp.util.rpc.AbstractOsgpServiceClientFact;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class PfOsgpServiceSmIntClientFact extends AbstractOsgpServiceClientFact {

	private static PfOsgpServiceSmIntClientFact sInstance = null;
	
	private static Config config = ConfigFactory.load("platform");
	
	private PfOsgpServiceSmIntClientFact(Class<? extends AbstractOsgpServiceClient> serviceClientClass, List<Server> servers) {
		super(serviceClientClass, servers);
	}
	
	public static PfOsgpServiceSmIntClient client() {
		if (sInstance == null) {
			List<Server> servers = ConfigHelper.getServers(config, "grpc.osgp-service.smint");
			sInstance = new PfOsgpServiceSmIntClientFact(PfOsgpServiceSmIntClient.class, servers);
		}
		return (PfOsgpServiceSmIntClient) sInstance.getClient();
	}
}
