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
import com.alliander.osgp.shared.NotificationMsg;
import com.alliander.osgp.shared.NotificationServiceGrpc;
import com.alliander.osgp.shared.RequestResponseMsg;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public abstract class AbstractNotificationServiceClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNotificationServiceClient.class.getName());

	private ManagedChannel streamChannel;
	private ManagedChannel blockingChannel;
	private NotificationServiceGrpc.NotificationServiceStub stub;
	private NotificationServiceGrpc.NotificationServiceBlockingStub blockingStub;

	private final CountDownLatch finishLatch = new CountDownLatch(1);
//	private StreamObserver<AckMsg> responseObserver;
//	private StreamObserver<NotificationMsg> reqObserver;

	protected AbstractNotificationServiceClient(final Server server) {
		int port = server.getPort();
		
		try {
			streamChannel = GrpcUtils.makeChannel(port);
			stub = NotificationServiceGrpc.newStub(streamChannel);
			
			blockingChannel = GrpcUtils.makeChannel(port); 
			blockingStub = NotificationServiceGrpc.newBlockingStub(blockingChannel);
		} catch (CertificateException | IOException e) {
			e.printStackTrace();
		}
		
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
	
	
	public void sendNotification(final NotificationMsg msg) {
		try {
			blockingStub.handleNotification(msg);
		} catch(StatusRuntimeException e) {
			LOGGER.warn("could not send notification "  + e);
		}		
	}
}
