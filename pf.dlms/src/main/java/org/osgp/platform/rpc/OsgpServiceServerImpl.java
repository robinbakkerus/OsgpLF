package org.osgp.platform.rpc;

import java.util.List;
import java.util.stream.Collectors;

import org.osgp.platform.dbs.PlatformDao;
import org.osgp.platform.dbs.PlatformDaoFact;
import org.osgp.platform.dbs.PlatformTable;
import org.osgp.platform.rpc.impl.HandleRequestImpl;
import org.osgp.platform.rpc.impl.HandleResponseImpl;
import org.osgp.platform.stats.PlatformStatistics;
import org.osgp.shared.CC;
import org.osgp.util.dao.PK;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.CorrelIdMsg;
import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.RequestStatsMsg;
import com.alliander.osgp.shared.ResponseStatus;
import com.alliander.osgp.shared.ResponsesMsg;
import com.alliander.osgp.shared.StatsMsg;
import com.google.protobuf.InvalidProtocolBufferException;

import io.grpc.stub.StreamObserver;

public class OsgpServiceServerImpl extends OsgpServiceGrpc.OsgpServiceImplBase implements CC {

//	private static Logger LOGGER = LoggerFactory.getLogger(OsgpServiceServerImpl.class);
	
	public OsgpServiceServerImpl() {
		super();
	}
	
	//----------- handle dlmsRequest -------------
	
	
	@Override
	public StreamObserver<RequestResponseMsg> handleRequest(StreamObserver<AckMsg> responseObserver) {
		HandleRequestImpl impl = new HandleRequestImpl();
		return impl.handleRequest(responseObserver);
	}
	

	//--------------- handle response -------------

	@Override
	public StreamObserver<RequestResponseMsg> handleResponse(StreamObserver<AckMsg> responseObserver) {
		HandleResponseImpl impl = new HandleResponseImpl();
		return impl.handleResponse(responseObserver);
	}

	// ---------- handle statistics -------------
	
	@Override
	public void getStatistics(RequestStatsMsg request, StreamObserver<StatsMsg> responseObserver) {
		responseObserver.onNext(PlatformStatistics.toStatsMsg());
		responseObserver.onCompleted();
	}

	@Override
	public void initStatistics(AckMsg request, StreamObserver<AckMsg> responseObserver) {
		responseObserver.onNext(AckMsg.newBuilder().build());
		PlatformStatistics.reset();
		responseObserver.onCompleted();
	}
	
	//----- handle Ping ---
	
	@Override
	public void ping(AckMsg request, StreamObserver<AckMsg> responseObserver) {
		responseObserver.onNext(AckMsg.newBuilder().build());
		responseObserver.onCompleted();
	}
	
	//----- getResponse ---
	
	@Override
	public void getResponse(CorrelIdMsg request, StreamObserver<RequestResponseMsg> responseObserver) {
		try {
			RequestResponseMsg reqRespMsg = dao().getResponse(request);
			responseObserver.onNext(reqRespMsg);
			responseObserver.onCompleted();
		} catch (InvalidProtocolBufferException e) {
			RequestResponseMsg r = RequestResponseMsg.newBuilder().build(); //TODO
			responseObserver.onNext(r);
			responseObserver.onCompleted();
		}
	}

	
	@Override
	public void getReadyResponses(AckMsg request, StreamObserver<ResponsesMsg> responseObserver) {
		List<RequestResponseMsg> allreqResp = dao().getAllRequestResponseMsgs();
		List<RequestResponseMsg> allReady = allreqResp.stream().filter(f -> isDone(f)).collect(Collectors.toList());
		List<PK> delReqResp = allReady.stream().map(f -> new PK(f.getCorrelId())).collect(Collectors.toList());
		sendResponsesToSmInt(responseObserver, allReady);
		dao().deleteList(PlatformTable.REQ_RESP, delReqResp);
	}

	private void sendResponsesToSmInt(StreamObserver<ResponsesMsg> responseObserver,
			List<RequestResponseMsg> allReady) {
		ResponsesMsg result = ResponsesMsg.newBuilder().addAllResponses(allReady).build();
		responseObserver.onNext(result);
		responseObserver.onCompleted();
	} 

	private boolean isDone(final RequestResponseMsg reqrespMsg) {
		return reqrespMsg.getResponse().getStatus() != null && !reqrespMsg.getResponse().getStatus().equals(ResponseStatus.NOT_SET);
	}
	
	//----------------- helper methods ----------
	private PlatformDao dao() {
		return PlatformDaoFact.INSTANCE.getDao();
	}

}
