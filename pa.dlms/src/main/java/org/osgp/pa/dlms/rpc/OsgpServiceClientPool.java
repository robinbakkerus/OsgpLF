package org.osgp.pa.dlms.rpc;

import java.util.concurrent.TimeUnit;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 * This (singleton) class maintains OsgpServiceClient's that respond to the core.
 * We want to use the stream facility of gPrc so we collect responses, 
 * and trigger Core when either a threshold is met or the last incoming request is old.
 * In that case this client must be repaced with a fresh one.  
 * @author robin
 *
 */
public class OsgpServiceClientPool {

	private static OsgpServiceClientPool sInstance = null;
	
	private static Config config = ConfigFactory.load("dlms");
	
	private OsgpServiceCoreClient currClient = null;
	
	private ActorSystem system;
	
	public static OsgpServiceClientPool initialize(ActorSystem system) {
		sInstance = new OsgpServiceClientPool(system);
		return sInstance;
	}
	
	public static OsgpServiceClientPool getInstance() {
		if (sInstance == null) {
			throw new RuntimeException("OsgpServiceClientPool is not initialized");
		}
		return sInstance;
	}
	
	private OsgpServiceClientPool(final ActorSystem system) {
		this.system = system;
	}
	
	public synchronized OsgpServiceCoreClient getClient() {
		if (currClient == null) {
			currClient = makeNewClient();
		} else {
			if (currClient.isAlreadyClosed()) {
				currClient = makeNewClient();
			}
		} 
		return currClient;
	}
	
	private OsgpServiceCoreClient makeNewClient() {
		OsgpServiceCoreClient client = OsgpServiceCoreClientFact.client();
		
		if (client != null) {
			int waitFor = config.getInt("grpc.response-stream.time");
			FiniteDuration duration = Duration.create(waitFor, TimeUnit.SECONDS);
			system.scheduler().scheduleOnce(duration,   new Runnable() {
			    @Override
			    public void run() {
			    	client.triggerService();
			    }
			}, system.dispatcher());
		}

		return client;
	}
}
