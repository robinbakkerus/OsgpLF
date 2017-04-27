package org.osgp.pa.dlms.dlms.request;

import java.util.Map;

import org.osgp.util.RequestHandler;
import org.osgp.util.ScanClassesHelper;

import akka.actor.ActorSystem;

public class RequestHandlerFact {

	private static Map<String, RequestHandler> requestHandlers;
	
	public static void initialize() {
		if (requestHandlers == null) {
			String pck = RequestHandlerFact.class.getPackage().getName();
			requestHandlers = ScanClassesHelper.fillRequestHandlersMap(pck);
		}
	}

	public static RequestHandler get(final String classname, final ActorSystem system) {
		int dotAt = classname.indexOf(".");
		final String name = dotAt > 0 ? classname.substring(dotAt+1) : classname;
		RequestHandler result = requestHandlers.get(name);
		return result;
	}
}
