package org.osgp.smint.rpc;

import org.osgp.smint.rpc.impl.HandleNotificationImpl;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.NotificationMsg;
import com.alliander.osgp.shared.NotificationServiceGrpc;

import io.grpc.stub.StreamObserver;

public class SmIntNotificationServer extends NotificationServiceGrpc.NotificationServiceImplBase {

	public SmIntNotificationServer() {
		super();
	}

	@Override
	public void handleNotification(NotificationMsg request, StreamObserver<AckMsg> responseObserver) {
		new HandleNotificationImpl().handleNotification(request, responseObserver);
	}

	@Override
	public void ping(AckMsg request, StreamObserver<AckMsg> responseObserver) {
		responseObserver.onNext(AckMsg.newBuilder().build());
		responseObserver.onCompleted();
	}

	

}
