package org.osgp.smint.rpc.impl;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import org.osgp.shared.CC;
import org.osgp.smint.SmIntAkkaServer;
import org.osgp.smint.actor.RequestResponseMsgWrapper;
import org.osgp.util.rpc.GrpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.NotificationMsg;
import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.ResponsesMsg;

import akka.actor.ActorRef;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

public class HandleNotificationImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(HandleNotificationImpl.class);

	private ManagedChannel channel = null;
	private OsgpServiceGrpc.OsgpServiceBlockingStub blockingStub = null;

	public HandleNotificationImpl() {
		super();
	}

	public void handleNotification(NotificationMsg request, StreamObserver<AckMsg> responseObserver) {
		LOGGER.debug("got notification {}", request);
		try {
			initialize();
			responseObserver.onNext(AckMsg.newBuilder().build());
			responseObserver.onCompleted();
			final ResponsesMsg responses = blockingStub.getReadyResponses(AckMsg.newBuilder().build());
			devopsActor().tell(new RequestResponseMsgWrapper(responses.getResponsesList()), ActorRef.noSender());
		} catch (Exception ex) {
			LOGGER.error("error getting responses " + ex);
		} finally {
			shutdown();
		}
	}

	private void initialize() {
		try {
			channel = GrpcUtils.makeChannel(CC.GPRC_DLMSSRV_PLATFORM_PORT);
			blockingStub = OsgpServiceGrpc.newBlockingStub(channel);
		} catch (CertificateException | IOException e) {
			LOGGER.error("error ", e);
		}
	}

	private void shutdown() {
		try {
			channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOGGER.error("error ", e);
		}
	}

	private ActorRef devopsActor() {
		return SmIntAkkaServer.devsopsActor();
	}

}
