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

public class HandleResponseImpl extends OsgpServiceGrpc.OsgpServiceImplBase implements CC {

	private static Logger LOGGER = LoggerFactory.getLogger(HandleResponseImpl.class);
	
	public HandleResponseImpl() {
		super();
	}

	public StreamObserver<RequestResponseMsg> handleResponse(StreamObserver<AckMsg> responseObserver) {
		return new StreamObserver<RequestResponseMsg>() {

			long startTime = System.nanoTime();

			final List<RequestResponseMsg> allMsg = new ArrayList<>();
			
			@Override
			public void onNext(RequestResponseMsg reqRespMsg) {
				allMsg.add(RequestResponseMsg.newBuilder(reqRespMsg).build());
				PlatformStatistics.incResponsesIn();
			}
			
			@Override
			public void onCompleted() {
				responseObserver.onNext(makeResponseMsg(startTime));
				responseObserver.onCompleted();		
				final ActorRef actor = AkkaPlatformSystem.responseHandlerActor();
				final RequestResponseListMsg requestResponseListMsg = RequestResponseListMsg.newBuilder().addAllRequestResponses(allMsg).build();
				actor.tell(requestResponseListMsg, ActorRef.noSender());
			}

			@Override
			public void onError(Throwable arg0) {
				LOGGER.error("Platform handleResponse cancelled");
			}
		};
	}
	
	private AckMsg makeResponseMsg(long startTime) {
		long seconds = NANOSECONDS.toSeconds(System.nanoTime() - startTime);
		return AckMsg.newBuilder().setStatus("processed all request in " + seconds).build();
	}
}
