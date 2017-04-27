package org.osgp.platform.rpc.impl;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.ArrayList;
import java.util.List;

import org.osgp.platform.AkkaPlatformSystem;
import org.osgp.platform.stats.PlatformStatistics;
import org.osgp.shared.CC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.RequestResponseListMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

import akka.actor.ActorRef;
import io.grpc.stub.StreamObserver;

public class HandleRequestImpl extends OsgpServiceGrpc.OsgpServiceImplBase implements CC {

	private static Logger LOGGER = LoggerFactory.getLogger(HandleRequestImpl.class);
	
	public HandleRequestImpl() {
		super();
	}
	
	@Override
	public StreamObserver<RequestResponseMsg> handleRequest(StreamObserver<AckMsg> responseObserver) {
		LOGGER.debug("rpc dlmsRequest triggered");
		return new StreamObserver<RequestResponseMsg>() {

			long startTime = System.nanoTime();
			
			final List<RequestResponseMsg> allMsg = new ArrayList<>();
			
			@Override
			public void onNext(RequestResponseMsg dlmsReqRespMsg) {
				try {
					allMsg.add(RequestResponseMsg.newBuilder(dlmsReqRespMsg).build());
					PlatformStatistics.incRequestsIn();
				} catch (Exception ex) {
					LOGGER.error("error handling rpc " + dlmsReqRespMsg.getCommon().getDeviceId() + ", " + ex);
				}
			}

			@Override
			public void onError(Throwable t) {
				LOGGER.error("dlmsRequest cancelled");
			}

			@Override
			public void onCompleted() {
				final AckMsg ackMsg = makeResponseMsg(startTime);
				responseObserver.onNext(ackMsg);
				responseObserver.onCompleted();
				printProgress(allMsg, ackMsg);
				processAllDlmsReqRespMsg(allMsg);
			}

		};
	}
	
	private void processAllDlmsReqRespMsg(final List<RequestResponseMsg> allMsg) {
		final RequestResponseListMsg reqRespList = RequestResponseListMsg.newBuilder().addAllRequestResponses(allMsg).build();
		AkkaPlatformSystem.requestHandlerActor().tell(reqRespList, ActorRef.noSender());
	}


	//----------------- helper methods ----------

	private AckMsg makeResponseMsg(long startTime) {
		long seconds = NANOSECONDS.toSeconds(System.nanoTime() - startTime);
		return AckMsg.newBuilder().setStatus("processed all request in " + seconds).build();
	}
	
	private void printProgress(final List<RequestResponseMsg> allMsg, final AckMsg ackMsg) {
		LOGGER.info("allMsg.size = " + allMsg.size() + " " + ackMsg);
	}
}
