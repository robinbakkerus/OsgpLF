package org.osgp.core.rpc;

import java.util.List;

import org.osgp.util.ConfigHelper;
import org.osgp.util.Server;
import org.osgp.util.rpc.AbstractOsgpServiceClient;
import org.osgp.util.rpc.AbstractOsgpServiceClientFact;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class OsgpServicePFClientFact extends AbstractOsgpServiceClientFact {

	private static OsgpServicePFClientFact sInstance = null;
	
	private static Config config = ConfigFactory.load("core");
	
	private OsgpServicePFClientFact(Class<? extends AbstractOsgpServiceClient> serviceClientClass, List<Server> servers) {
		super(serviceClientClass, servers);
	}
	
	public static OsgpServicePFClient client() {
		if (sInstance == null) {
			List<Server> servers = ConfigHelper.getServers(config, "grpc.osgp-service.platform");
			sInstance = new OsgpServicePFClientFact(OsgpServicePFClient.class, servers);
		}
		return (OsgpServicePFClient) sInstance.getClient();
	}
}
