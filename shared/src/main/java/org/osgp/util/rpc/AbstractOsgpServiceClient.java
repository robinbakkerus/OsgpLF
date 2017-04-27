package org.osgp.util.rpc;


import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.osgp.shared.exceptionhandling.ComponentType;
import org.osgp.shared.exceptionhandling.TechnicalException;
import org.osgp.util.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.RequestResponseListMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public abstract class AbstractOsgpServiceClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOsgpServiceClient.class.getName());

	private ManagedChannel streamChannel;
	private ManagedChannel blockingChannel;
	protected OsgpServiceGrpc.OsgpServiceStub stub;
	protected OsgpServiceGrpc.OsgpServiceBlockingStub blockingStub;

	private final CountDownLatch finishLatch = new CountDownLatch(1);
	private StreamObserver<AckMsg> responseObserver;
	private StreamObserver<RequestResponseMsg> requestObserver;

	protected AbstractOsgpServiceClient(final Server server) {
		int port = server.getPort();
		
		try {
			streamChannel = GrpcUtils.makeChannel(port);
			stub = OsgpServiceGrpc.newStub(streamChannel);
			
			blockingChannel = GrpcUtils.makeChannel(port); 
			blockingStub = OsgpServiceGrpc.newBlockingStub(blockingChannel);
		} catch (CertificateException | IOException e) {
			LOGGER.error("error get grpc client {}", e.getMessage());
		}
		
		responseObserver = makeResponseObserver(finishLatch);
		if ("handleRequest".equals(server.getRpc())) {
			requestObserver = stub.handleRequest(responseObserver);
		} else if ("handleResponse".equals(server.getRpc())) {
			requestObserver = stub.handleResponse(responseObserver);
		} else if ("handleResponses".equals(server.getRpc()) || "handleRequest".equals(server.getRpc())) {
			requestObserver = null;
		} else {
			String msg = "no rpc named "  +server.getRpc() + " supported";
			LOGGER.error(msg);
			TechnicalException te = new TechnicalException(ComponentType.OSGP_CORE, msg);
			throw new RuntimeException(msg, te);
		}
	}

	public void addNextRequest(final RequestResponseMsg reqRespMsg) {
		if (finishLatch.getCount() == 0) {
			return;
		}
		requestObserver.onNext(reqRespMsg);
	}

	public boolean ping() {
		try {
			blockingStub.ping(AckMsg.newBuilder().build());
			return true;
		} catch(StatusRuntimeException e) {
			LOGGER.warn("could not ping "  + e);
			return false;
		}
	}
	
	public AckMsg handleRequests(final RequestResponseListMsg requestResponseListMsg) {
		return blockingStub.handleRequestList(requestResponseListMsg);
	}

	public AckMsg handleResponses(final RequestResponseListMsg requestResponseListMsg) {
		return blockingStub.handleResponseList(requestResponseListMsg);
	}

	public void shutdown() {
		try {
			Thread.sleep(1000);
			streamChannel.shutdown().awaitTermination(1, TimeUnit.MINUTES);
			blockingChannel.shutdown().awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			LOGGER.error("shutdown interrupted {}", e.getMessage());
		}
	}

	public void setComplete() {
		requestObserver.onCompleted();

		try {
			if (!finishLatch.await(1, TimeUnit.MINUTES)) {
				LOGGER.error("error completing grpc servcice");
			}
		} catch (InterruptedException e) {
			LOGGER.error("setcomplete interrupted {}", e.getMessage());
		}
	}

	private StreamObserver<AckMsg> makeResponseObserver(CountDownLatch finishLatch) {
		return new StreamObserver<AckMsg>() {
			@Override
			public void onNext(AckMsg ack) {
				LOGGER.info(ack.getStatus());
			}

			@Override
			public void onError(Throwable t) {
				LOGGER.error(t.getMessage());
				finishLatch.countDown();
			}

			@Override
			public void onCompleted() {
				finishLatch.countDown();
			}
		};
	}

}
