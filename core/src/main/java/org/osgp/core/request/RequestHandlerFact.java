package org.osgp.core.request;

import java.util.Map;

import org.osgp.util.RequestHandler;
import org.osgp.util.ScanClassesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandlerFact {

	protected static Logger LOGGER = LoggerFactory.getLogger(RequestHandlerFact.class);
	
	private static Map<String, RequestHandler> requestHandlers;
	
	public static void initialize() {
		if (requestHandlers == null) {
			String pck = RequestHandlerFact.class.getPackage().getName();
			requestHandlers = ScanClassesHelper.fillRequestHandlersMap(pck);
			LOGGER.debug(requestHandlers.toString());
		}
	}

	public static RequestHandler get(String classname) {
		int dotAt = classname.indexOf(".");
		final String name = dotAt > 0 ? classname.substring(dotAt+1) : classname;
		return requestHandlers.get(name);
	}
}
