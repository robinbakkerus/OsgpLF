package org.osgp.util.rpc;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.osgp.util.RoundRobin;
import org.osgp.util.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractNotificationServiceClientFact {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNotificationServiceClientFact.class.getName());

	protected RoundRobin roundRobin;
	
	protected List<Server> servers;
	
	protected Class<? extends AbstractNotificationServiceClient> serviceClientClass;
	
	protected AbstractNotificationServiceClientFact(Class<? extends AbstractNotificationServiceClient> serviceClientClass, List<Server> servers) {
		super();
		this.serviceClientClass = serviceClientClass;
		this.servers = servers;
		this.roundRobin = new RoundRobin(servers.size());
	}

	protected AbstractNotificationServiceClient getClient() {
		AbstractNotificationServiceClient result = null;
		
		int whileCount = 0;
		while (result == null && whileCount < roundRobin.getMax()) {
			int index = roundRobin.getIndex();
			AbstractNotificationServiceClient r = null;
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
