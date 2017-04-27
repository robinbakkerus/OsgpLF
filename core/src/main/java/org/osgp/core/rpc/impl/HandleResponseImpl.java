package org.osgp.core.rpc.impl;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.ArrayList;
import java.util.List;

import org.osgp.core.AkkaCoreSystem;
import org.osgp.core.rpc.OsgpServicePFClient;
import org.osgp.core.rpc.OsgpServicePFClientFact;
import org.osgp.core.stats.CoreStatistics;
import org.osgp.core.utils.RescheduleHelper;
import org.osgp.util.MsgMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.FlowMsg;
import com.alliander.osgp.shared.FlowPhase;
import com.alliander.osgp.shared.RequestResponseMsg;

import akka.actor.ActorRef;
import io.grpc.stub.StreamObserver;

public class HandleResponseImpl {

	private static Logger LOGGER = LoggerFactory.getLogger(HandleResponseImpl.class);

	public HandleResponseImpl() {
		super();
	}

	public StreamObserver<RequestResponseMsg> handleResponse(StreamObserver<AckMsg> responseObserver) {
		LOGGER.debug("rpc handleResponse triggered");

		return new StreamObserver<RequestResponseMsg>() {

			long startTime = System.nanoTime();

			final List<RequestResponseMsg> allMsg = new ArrayList<>();

			@Override
			public void onNext(RequestResponseMsg reqRespMsg) {
				FlowMsg flow = MsgMapper.makeFlow(reqRespMsg.getFlow(), FlowPhase.CORE_RESP_IN);
				allMsg.add(RequestResponseMsg.newBuilder(reqRespMsg).setFlow(flow).build());
				CoreStatistics.incResponsesIn();
			}

			@Override
			public void onCompleted() {
				final AckMsg ackMsg = makeResponseMsg(startTime);
				responseObserver.onNext(ackMsg);
				responseObserver.onCompleted();
				processResponses();
			}

			@Override
			public void onError(Throwable ex) {
				LOGGER.error("Core handleResponse cancelled: " + ex);
			}

			private void processResponses() {
				if (allMsg != null && allMsg.size() > 0) {
					printProgress(allMsg);
					final OsgpServicePFClient platformOsgpSrv = OsgpServicePFClientFact.client();
					if (platformOsgpSrv != null) {
						processAllResponses(platformOsgpSrv, allMsg);
						platformOsgpSrv.setComplete();
					} else {
						//TODO
					}
				}
			}
		};
	}

	private void processAllResponses(final OsgpServicePFClient platformOsgpSrv, final List<RequestResponseMsg> allMsg) {
		for (RequestResponseMsg reqRespMsg : allMsg) {
			if (RescheduleHelper.shouldBeRetried(reqRespMsg)) {
				 final ActorRef actor = AkkaCoreSystem.rescheduleActor();
				 actor.tell(RequestResponseMsg.newBuilder(reqRespMsg).build(), ActorRef.noSender());
			} else {
				platformOsgpSrv.addResponse(reqRespMsg);
				CoreStatistics.incResponsesOut();
			}
		}
	}

	private AckMsg makeResponseMsg(long startTime) {
		long seconds = NANOSECONDS.toMillis(System.nanoTime() - startTime);
		return AckMsg.newBuilder().setStatus("processed all request in " + seconds + " msecs").build();
	}
	
	private void printProgress(final List<RequestResponseMsg> allMsg) {
		LOGGER.debug("Core: allMsg.size = " + allMsg.size());
		LOGGER.debug(allMsg.get(0).getCorrelId() + " " + allMsg.get(0).getFlow());
		LOGGER.debug(allMsg.get(allMsg.size() - 1).getCorrelId()+ " " + allMsg.get(0).getFlow());
	}

}
