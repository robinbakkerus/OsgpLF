package org.osgp.core.rpc.impl;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.ArrayList;
import java.util.List;

import org.osgp.core.AkkaCoreSystem;
import org.osgp.core.stats.CoreStatistics;
import org.osgp.util.MsgMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.FlowMsg;
import com.alliander.osgp.shared.FlowPhase;
import com.alliander.osgp.shared.RequestResponseListMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

import akka.actor.ActorRef;
import io.grpc.stub.StreamObserver;

public class HandleRequestImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(HandleRequestImpl.class);

	public StreamObserver<RequestResponseMsg> handleRequest(StreamObserver<AckMsg> responseObserver) {
		LOGGER.debug("rpc handleRequest triggered");
		return new StreamObserver<RequestResponseMsg>() {

			long startTime = System.nanoTime();

			final List<RequestResponseMsg> allMsg = new ArrayList<>();

			@Override
			public void onNext(RequestResponseMsg reqRespMsg) {
				FlowMsg flow = MsgMapper.makeFlow(reqRespMsg.getFlow(), FlowPhase.CORE_REQ_IN);
				allMsg.add(RequestResponseMsg.newBuilder(reqRespMsg).setFlow(flow).build());
				CoreStatistics.incRequestsIn();
			}

			@Override
			public void onCompleted() {
				final AckMsg ackMsg = makeResponseMsg(startTime);
				responseObserver.onNext(ackMsg);
				responseObserver.onCompleted();
				if (!allMsg.isEmpty()) {
					printProgress(allMsg, ackMsg);
					processAllReqRespMsg(allMsg);
				}
			}

			@Override
			public void onError(Throwable ex) {
				LOGGER.error("Core handleRequest cancelled: {} ",ex.getMessage(), ex);
			}
		};
	}

	private void processAllReqRespMsg(final List<RequestResponseMsg> allMsg) {
		LOGGER.debug("rpc processAllReqRespMsg called with {} items", allMsg.size());
		final RequestResponseListMsg msg = RequestResponseListMsg.newBuilder().addAllRequestResponses(allMsg).build();
		AkkaCoreSystem.requestHandlerActor().tell(msg, ActorRef.noSender());
	}
	
	private AckMsg makeResponseMsg(long startTime) {
		long seconds = NANOSECONDS.toMillis(System.nanoTime() - startTime);
		return AckMsg.newBuilder().setStatus("processed all request in " + seconds + " msecs").build();
	}


	private void printProgress(final List<RequestResponseMsg> allMsg, final AckMsg ackMsg) {
		LOGGER.debug("Core: allMsg.size = {], ackMsg = {}", allMsg.size(), ackMsg);
		LOGGER.debug(allMsg.get(0).getCorrelId() + " " + allMsg.get(0).getFlow());
	}

		
}
