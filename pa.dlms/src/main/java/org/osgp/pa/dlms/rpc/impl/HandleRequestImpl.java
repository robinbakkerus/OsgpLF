package org.osgp.pa.dlms.rpc.impl;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.osgp.pa.dlms.dlms.request.RequestHandlerFact;
import org.osgp.pa.dlms.dlms.stats.DlmsStatistics;
import org.osgp.util.MsgMapper;
import org.osgp.util.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.ActionMsg;
import com.alliander.osgp.shared.FlowMsg;
import com.alliander.osgp.shared.FlowPhase;
import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.Descriptors.FieldDescriptor;

import akka.actor.ActorSystem;
import io.grpc.stub.StreamObserver;

public class HandleRequestImpl extends OsgpServiceGrpc.OsgpServiceImplBase {

	private static Logger LOGGER = LoggerFactory.getLogger(HandleRequestImpl.class);

	private ActorSystem actorSystem;
	
	public HandleRequestImpl(ActorSystem actorSystem) {
		super();
		this.actorSystem = actorSystem;
	}


	public StreamObserver<RequestResponseMsg> handleRequest(StreamObserver<AckMsg> responseObserver) {
		LOGGER.debug("rpc handleRequest triggered" );
		
		return new StreamObserver<RequestResponseMsg>() {

			long startTime = System.nanoTime();

			final List<RequestResponseMsg> allMsg = new ArrayList<>();
			
			@Override
			public void onNext(RequestResponseMsg reqRespMsg) {
				FlowMsg flow = MsgMapper.makeFlow(reqRespMsg.getFlow(), FlowPhase.PA_REQ_IN);
				allMsg.add(RequestResponseMsg.newBuilder(reqRespMsg).setFlow(flow).build());
				DlmsStatistics.incRequestsIn();
			}
			
			@Override
			public void onCompleted() {
				final AckMsg ackMsg = makeResponseMsg(startTime);
				responseObserver.onNext(ackMsg);
				responseObserver.onCompleted();		
				if (allMsg != null && allMsg.size() > 0) {
					printProgress(allMsg, ackMsg);
					processAllDlmsReqRespMsg(ackMsg, allMsg);
				}
			}

			@Override
			public void onError(Throwable ex) {
				LOGGER.error("Dlms handleRequest cancelled: " + ex);
			}
		};
	}

	private void processAllDlmsReqRespMsg(final AckMsg ackMsg, final List<RequestResponseMsg> allMsg) {
		
		for (FieldDescriptor fd : ActionMsg.getDescriptor().getFields()) {
			List<RequestResponseMsg> filterMsgs = allMsg.stream().filter(m -> m.getAction().hasField(fd))
					.collect(Collectors.toList());	
			if (!filterMsgs.isEmpty()) {
				RequestHandler handler = RequestHandlerFact.get(fd.getMessageType().getFullName(), actorSystem);
				handler.handleRequests(filterMsgs);
			}
		}
	}

	private AckMsg makeResponseMsg(long startTime) {
		long seconds = NANOSECONDS.toMillis(System.nanoTime() - startTime);
		return AckMsg.newBuilder().setStatus("processed all request in " + seconds + " msecs").build();
	}

	//----------- helper methods -----
	private void printProgress(final List<RequestResponseMsg> allMsg, final AckMsg ackMsg) {
		LOGGER.debug("Dlms: allMsg.size = " + allMsg.size() + " " + ackMsg);
		LOGGER.debug(allMsg.get(0).getCorrelId());
		LOGGER.debug(allMsg.get(allMsg.size()-1).getCorrelId());
	}

	
}
