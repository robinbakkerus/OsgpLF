package org.osgp.core.actor;

import java.util.List;
import java.util.stream.Collectors;

import org.osgp.core.request.RequestHandlerFact;
import org.osgp.shared.CC;
import org.osgp.util.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.ActionMsg;
import com.alliander.osgp.shared.RequestResponseListMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.Descriptors.FieldDescriptor;

import akka.actor.UntypedActor;

public class CoreRequestsHandlerActor extends UntypedActor implements CC {

	private static final Logger LOGGER = LoggerFactory.getLogger(CoreRequestsHandlerActor.class.getName());

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof RequestResponseListMsg) {
			procesScheduledMsgs((RequestResponseListMsg) msg);
		}
	}

	private void procesScheduledMsgs(final RequestResponseListMsg requestResponseList) {
		final List<RequestResponseMsg> allReqResp = requestResponseList.getRequestResponsesList();
		for (FieldDescriptor fd : ActionMsg.getDescriptor().getFields()) {
			List<RequestResponseMsg> filterMsgs = allReqResp.stream().filter(m -> m.getAction().hasField(fd))
					.collect(Collectors.toList());	
			if (!filterMsgs.isEmpty()) {
				RequestHandler handler = RequestHandlerFact.get(fd.getMessageType().getFullName());
				handler.handleRequests(filterMsgs);
			}
		}
	}


}
