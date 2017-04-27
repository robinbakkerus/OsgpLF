package org.osgp.core.rpc;

import java.util.List;

import org.osgp.util.ConfigHelper;
import org.osgp.util.Server;
import org.osgp.util.rpc.AbstractOsgpServiceClient;
import org.osgp.util.rpc.AbstractOsgpServiceClientFact;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class OsgpServicePAClientFact extends AbstractOsgpServiceClientFact {

	private static OsgpServicePAClientFact sInstance = null;
	
	private static Config config = ConfigFactory.load("core");
	
	private OsgpServicePAClientFact(Class<? extends AbstractOsgpServiceClient> serviceClientClass, List<Server> servers) {
		super(serviceClientClass, servers);
	}
	
	public static OsgpServicePAClient client() {
		if (sInstance == null) {
			List<Server> servers = ConfigHelper.getServers(config, "grpc.osgp-service.dlms");
			sInstance = new OsgpServicePAClientFact(OsgpServicePAClient.class, servers);
		}
		return (OsgpServicePAClient) sInstance.getClient();
	}
}
