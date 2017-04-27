package org.osgp.util.rpc;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.osgp.util.RoundRobin;
import org.osgp.util.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOsgpServiceClientFact {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOsgpServiceClientFact.class.getName());

	protected RoundRobin roundRobin;
	
	protected List<Server> servers;
	
	protected Class<? extends AbstractOsgpServiceClient> serviceClientClass;
	
	protected AbstractOsgpServiceClientFact(Class<? extends AbstractOsgpServiceClient> serviceClientClass, List<Server> servers) {
		super();
		this.serviceClientClass = serviceClientClass;
		this.servers = servers;
		this.roundRobin = new RoundRobin(servers.size());
	}

	protected AbstractOsgpServiceClient getClient() {
		AbstractOsgpServiceClient result = null;
		
		int whileCount = 0;
		while (result == null && whileCount < roundRobin.getMax()) {
			int index = roundRobin.getIndex();
			AbstractOsgpServiceClient r = null;
			try {
				r = serviceClientClass.getDeclaredConstructor(Server.class).newInstance(servers.get(index));
				roundRobin.nextIndex();
				if (r.ping())  {
					result = r;
				} 
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}

			whileCount++;
		}
		
		if (result == null) {
			LOGGER.error("No active server found for " + servers);
		}
		return result;
	}
}
