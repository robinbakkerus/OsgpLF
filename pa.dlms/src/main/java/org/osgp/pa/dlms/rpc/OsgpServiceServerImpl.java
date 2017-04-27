package org.osgp.pa.dlms.rpc;

import org.osgp.pa.dlms.dlms.stats.DlmsStatistics;
import org.osgp.pa.dlms.rpc.impl.HandleRequestImpl;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.RequestStatsMsg;
import com.alliander.osgp.shared.StatsMsg;

import akka.actor.ActorSystem;
import io.grpc.stub.StreamObserver;

public class OsgpServiceServerImpl extends OsgpServiceGrpc.OsgpServiceImplBase {

//	private static Logger LOGGER = LoggerFactory.getLogger(OsgpServiceServerImpl.class);

	private ActorSystem actorSystem;
	
	public OsgpServiceServerImpl(ActorSystem actorSystem) {
		super();
		this.actorSystem = actorSystem;
	}


	// ------------ handle request -------
	
	@Override
	public StreamObserver<RequestResponseMsg> handleRequest(StreamObserver<AckMsg> responseObserver) {
		HandleRequestImpl impl = new HandleRequestImpl(actorSystem);
		return impl.handleRequest(responseObserver);
	}
	
	// ---------- handle statistics -------------
	
	@Override
	public void getStatistics(RequestStatsMsg request, StreamObserver<StatsMsg> responseObserver) {
		responseObserver.onNext(DlmsStatistics.toStatsMsg());
		responseObserver.onCompleted();
	}

	@Override
	public void initStatistics(AckMsg request, StreamObserver<AckMsg> responseObserver) {
		responseObserver.onNext(AckMsg.newBuilder().build());
		DlmsStatistics.reset();
		responseObserver.onCompleted();
	}
	
	//----- hande Ping ----
	

	@Override
	public void ping(AckMsg request, StreamObserver<AckMsg> responseObserver) {
		responseObserver.onNext(AckMsg.newBuilder().build());
		responseObserver.onCompleted();
	}

	
}
