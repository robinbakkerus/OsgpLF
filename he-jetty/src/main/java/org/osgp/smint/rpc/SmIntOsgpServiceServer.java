package org.osgp.smint.rpc;

import org.osgp.smint.SmIntAkkaServer;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.RequestResponseListMsg;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import io.grpc.stub.StreamObserver;

public class SmIntOsgpServiceServer extends OsgpServiceGrpc.OsgpServiceImplBase {

//	private static Logger LOGGER = LoggerFactory.getLogger(OsgpServiceServerImpl.class);

	private ActorSystem actorSystem;
	
	public SmIntOsgpServiceServer(ActorSystem actorSystem) {
		super();
		this.actorSystem = actorSystem;
	}


	//----- handle Ping ----
	
	@Override
	public void ping(AckMsg request, StreamObserver<AckMsg> responseObserver) {
		responseObserver.onNext(AckMsg.newBuilder().build());
		responseObserver.onCompleted();
	}


	@Override
	public void handleResponseList(RequestResponseListMsg requestResponseList, StreamObserver<AckMsg> responseObserver) {
		responseObserver.onNext(AckMsg.newBuilder().build());
		responseObserver.onCompleted();
		SmIntAkkaServer.devsopsActor().tell(requestResponseList, ActorRef.noSender());
	}
	
}
