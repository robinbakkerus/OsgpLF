package org.osgp.pa.dlms.rpc;

import java.util.List;

import org.osgp.util.ConfigHelper;
import org.osgp.util.Server;
import org.osgp.util.rpc.AbstractOsgpServiceClient;
import org.osgp.util.rpc.AbstractOsgpServiceClientFact;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class OsgpServiceCoreClientFact extends AbstractOsgpServiceClientFact {

	private static OsgpServiceCoreClientFact sInstance = null;
	
	private static Config config = ConfigFactory.load("dlms");
	
	private OsgpServiceCoreClientFact(Class<? extends AbstractOsgpServiceClient> serviceClientClass, List<Server> servers) {
		super(serviceClientClass, servers);
	}
	
	public static OsgpServiceCoreClient client() {
		if (sInstance == null) {
			List<Server> servers = ConfigHelper.getServers(config, "grpc.osgp-service.core");
			sInstance = new OsgpServiceCoreClientFact(OsgpServiceCoreClient.class, servers);
		}
		return (OsgpServiceCoreClient) sInstance.getClient();
	}
}
