package org.osgp.audittrail.rpc;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.ArrayList;
import java.util.List;

import org.osgp.audittrail.dao.AuditTrailDao;
import org.osgp.audittrail.dao.AuditTrailDaoFact;
import org.osgp.shared.CC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.AuditTrailServiceGrpc;
import com.alliander.osgp.shared.RequestResponseMsg;

import io.grpc.stub.StreamObserver;

public class AuditTrailServiceImpl extends AuditTrailServiceGrpc.AuditTrailServiceImplBase implements CC {

	private static Logger LOGGER = LoggerFactory.getLogger(AuditTrailServiceImpl.class);

	public AuditTrailServiceImpl() {
		super();
	}
	
	// --- saveAuditTrail 

	@Override
	public StreamObserver<RequestResponseMsg> saveAuditTrail(StreamObserver<AckMsg> responseObserver) {
		LOGGER.debug("rpc saveAuditTrail triggered");
		return new StreamObserver<RequestResponseMsg>() {

			long startTime = System.nanoTime();

			final List<RequestResponseMsg> allMsg = new ArrayList<>();

			@Override
			public void onNext(RequestResponseMsg reqRespMsg) {
				allMsg.add(RequestResponseMsg.newBuilder(reqRespMsg).build());
			}

			@Override
			public void onCompleted() {
				final AckMsg ackMsg = makeResponseMsg(startTime);
				responseObserver.onNext(ackMsg);
				responseObserver.onCompleted();
				if (allMsg != null && allMsg.size() > 0) {
					processAllReqRespMsg(allMsg);
				}
			}

			@Override
			public void onError(Throwable arg0) {
				LOGGER.error("saveAuditTrail cancelled");
			}
		};
	}
	
	
	private void processAllReqRespMsg(final List<RequestResponseMsg> allMsg) {
		for (RequestResponseMsg reqRespMsg : allMsg) {
			dao().saveRequestResponseMsg(reqRespMsg);
		}
	}

	
	private AckMsg makeResponseMsg(long startTime) {
		long seconds = NANOSECONDS.toMillis(System.nanoTime() - startTime);
		return AckMsg.newBuilder().setStatus("processed all request in " + seconds + " msecs").build();
	}

	private AuditTrailDao dao() {
		return AuditTrailDaoFact.INSTANCE.getDao();
	}


	// --- ping --
	
	@Override
	public void ping(AckMsg request, StreamObserver<AckMsg> responseObserver) {
		responseObserver.onNext(AckMsg.newBuilder().build());
		responseObserver.onCompleted();
	}


}
