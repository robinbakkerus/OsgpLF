package org.osgp.platform.rpc.client;

import java.util.List;

import org.osgp.util.ConfigHelper;
import org.osgp.util.Server;
import org.osgp.util.rpc.AbstractAuditTrailServiceClient;
import org.osgp.util.rpc.AbstractAuditTrailServiceClientFact;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class PfAuditTrailServiceClientFact extends AbstractAuditTrailServiceClientFact {

	private static PfAuditTrailServiceClientFact sInstance = null;
	
	private static Config config = ConfigFactory.load("platform");
	
	private PfAuditTrailServiceClientFact(Class<? extends AbstractAuditTrailServiceClient> serviceClientClass, List<Server> servers) {
		super(serviceClientClass, servers);
	}
	
	public static PfAuditTrailServiceClient client() {
		if (sInstance == null) {
			List<Server> servers = ConfigHelper.getServers(config, "grpc.audit-trail-service");
			sInstance = new PfAuditTrailServiceClientFact(PfAuditTrailServiceClient.class, servers);
		}
		return (PfAuditTrailServiceClient) sInstance.getClient();
	}
}
