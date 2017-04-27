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
import com.alliander.osgp.shared.AuditTrailServiceGrpc;
import com.alliander.osgp.shared.RequestResponseMsg;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public abstract class AbstractAuditTrailServiceClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAuditTrailServiceClient.class.getName());

	private ManagedChannel streamChannel;
	private ManagedChannel blockingChannel;
	private AuditTrailServiceGrpc.AuditTrailServiceStub stub;
	private AuditTrailServiceGrpc.AuditTrailServiceBlockingStub blockingStub;

	private final CountDownLatch finishLatch = new CountDownLatch(1);
	private StreamObserver<AckMsg> responseObserver;
	private StreamObserver<RequestResponseMsg> reqObserver;

	protected AbstractAuditTrailServiceClient(final Server server) {
		int port = server.getPort();
		
		try {
			streamChannel = GrpcUtils.makeChannel(port);
			stub = AuditTrailServiceGrpc.newStub(streamChannel);
			
			blockingChannel = GrpcUtils.makeChannel(port); //tuple2.getChannel();
			blockingStub = AuditTrailServiceGrpc.newBlockingStub(blockingChannel);
		} catch (CertificateException | IOException e) {
			e.printStackTrace();
		}
		
		responseObserver = makeResponseObserver(finishLatch);
		if ("saveAuditTrail".equals(server.getRpc())) {
			reqObserver = stub.saveAuditTrail(responseObserver);
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
		reqObserver.onNext(reqRespMsg);
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
	
	public void shutdown() {
		try {
			Thread.sleep(1000);
			streamChannel.shutdown().awaitTermination(5, TimeUnit.MINUTES);
			blockingChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setComplete() {
		reqObserver.onCompleted();

		try {
			finishLatch.await(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
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
