package org.osgp.core.rpc;

import org.osgp.core.rpc.impl.HandleRequestImpl;
import org.osgp.core.rpc.impl.HandleResponseImpl;
import org.osgp.core.stats.CoreStatistics;
import org.osgp.shared.CC;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.RequestStatsMsg;
import com.alliander.osgp.shared.StatsMsg;

import io.grpc.stub.StreamObserver;

public class OsgpServiceServerImpl extends OsgpServiceGrpc.OsgpServiceImplBase implements CC {

	public OsgpServiceServerImpl() {
		super();
	}

	// --------------- handle request -------------

	@Override
	public StreamObserver<RequestResponseMsg> handleRequest(StreamObserver<AckMsg> responseObserver) {
		HandleRequestImpl impl = new HandleRequestImpl();
		return impl.handleRequest(responseObserver);
	}

	// --------------- handle response -------------

	@Override
	public StreamObserver<RequestResponseMsg> handleResponse(StreamObserver<AckMsg> responseObserver) {
		HandleResponseImpl impl = new HandleResponseImpl();
		return impl.handleResponse(responseObserver);
	}

	// ---------- handle statistics -------------
	
	@Override
	public void getStatistics(RequestStatsMsg request, StreamObserver<StatsMsg> responseObserver) {
		responseObserver.onNext(CoreStatistics.toStatsMsg());
		responseObserver.onCompleted();
	}


	@Override
	public void initStatistics(AckMsg request, StreamObserver<AckMsg> responseObserver) {
		responseObserver.onNext(AckMsg.newBuilder().build());
		CoreStatistics.reset();
		responseObserver.onCompleted();
	}

	//------------- handle Ping -----
	
	@Override
	public void ping(AckMsg request, StreamObserver<AckMsg> responseObserver) {
		responseObserver.onNext(AckMsg.newBuilder().build());
		responseObserver.onCompleted();
	}
	

}
