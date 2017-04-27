package org.osgp.platform.rpc.client;

import java.util.List;

import org.osgp.util.ConfigHelper;
import org.osgp.util.Server;
import org.osgp.util.rpc.AbstractOsgpServiceClient;
import org.osgp.util.rpc.AbstractOsgpServiceClientFact;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class PfOsgpServiceCoreClientFact extends AbstractOsgpServiceClientFact {

	private static PfOsgpServiceCoreClientFact sInstance = null;
	
	private static Config config = ConfigFactory.load("platform");
	
	private PfOsgpServiceCoreClientFact(Class<? extends AbstractOsgpServiceClient> serviceClientClass, List<Server> servers) {
		super(serviceClientClass, servers);
	}
	
	public static PfOsgpServiceCoreClient client() {
		if (sInstance == null) {
			List<Server> servers = ConfigHelper.getServers(config, "grpc.osgp-service.core");
			sInstance = new PfOsgpServiceCoreClientFact(PfOsgpServiceCoreClient.class, servers);
		}
		return (PfOsgpServiceCoreClient) sInstance.getClient();
	}
}
