package org.osgp.util;

import java.util.List;

import com.alliander.osgp.shared.RequestResponseMsg;

public interface RequestHandler {
	
	void ctorArguments(final Object ... args);

	void handleRequests(final List<RequestResponseMsg> reqRespMsgs);

}
