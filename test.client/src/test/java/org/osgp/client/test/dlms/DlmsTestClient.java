package org.osgp.client.test.dlms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import org.osgp.dlms.MsgUtils;
import org.osgp.util.CorrelId;
import org.osgp.util.rpc.GrpcUtils;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.DlmsSpecificMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.OsgpServiceGrpc.OsgpServiceStub;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.ResponseMsg;
import com.alliander.osgp.shared.ResponseStatus;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

public class DlmsTestClient {

	private static int index = 1;
	
	private static final int DLMS_PORT = 50053;
	private static final int CORE_PORT = 50052;
	private static final int PORT = CORE_PORT;
	
	public static void main(String[] args) throws Exception {
		ManagedChannel channel = GrpcUtils.makeChannel(PORT);
		OsgpServiceStub stub = OsgpServiceGrpc.newStub(channel);
		
		for (int i = 0; i < 80; i++) {
			final CountDownLatch finishLatch = new CountDownLatch(1);
			StreamObserver<AckMsg> observer = makeObserver(finishLatch);
			sendRequests(stub, finishLatch, observer);
			Thread.sleep(1000);
		}
	}
	
	private static StreamObserver<AckMsg> makeObserver(final CountDownLatch finishLatch) {
		StreamObserver<AckMsg> observer = new StreamObserver<AckMsg>() {

			@Override
			public void onNext(AckMsg arg0) {
				System.out.println("got ack " + arg0);
				
			}

			@Override
			public void onCompleted() {
				finishLatch.countDown();
			}

			@Override
			public void onError(Throwable arg0) {
				System.out.println("onError: " + arg0);
				
			}
		};
		return observer;
	}

	private static void sendRequests(OsgpServiceStub stub, final CountDownLatch finishLatch,
			StreamObserver<AckMsg> responseObserver) throws InterruptedException {
		
		StreamObserver<RequestResponseMsg> requestObserver = stub.handleRequest(responseObserver);
		try {
			for (int i = 0; i < 25000; i++) {
				RequestResponseMsg reqRespMsg = makeDlmsReqRespMsg("DEV" + index++);
				requestObserver.onNext(reqRespMsg);
				if (i % 10000 == 0)	
					System.out.println(i);
			}
			requestObserver.onCompleted();
		} catch (RuntimeException e) {
			// Cancel RPC
			requestObserver.onError(e);
			throw e;
		}

		// Receiving happens asynchronously
		if (!finishLatch.await(1, TimeUnit.MINUTES)) {
			System.out.println("recordRoute can not finish within 1 minutes");
		}
	}
	
	private static RequestResponseMsg makeDlmsReqRespMsg(final String devid) {
		DlmsSpecificMsg specificMsg = DlmsSpecificMsg.newBuilder()
				.addAllActions(makeActionList())
				.build();

		return RequestResponseMsg.newBuilder()
				.setCommon(makeCommon(devid))
				.setCorrelId(CorrelId.generate())
				.setAction(MsgUtils.makeDlmsAction(specificMsg))
				.setResponse(makeResponseMsg())
				.build();
	}

	private static CommonMsg makeCommon(final String devid) {
		return CommonMsg.newBuilder()
				.setApplicationName("Appname")
				.setOrganisation("Infostroom")
				.setDeviceId(devid)
				.setJobId(1L)
				.setUserName("robinb").build();
	}
	
	private static ResponseMsg makeResponseMsg() {
		return ResponseMsg.newBuilder().setAction("SpecificAction")
				.setStatus(ResponseStatus.SUBMITTED)
				.build();
	}
	
	private static List<DlmsActionMsg> makeActionList() {
		List<DlmsActionMsg> r = new ArrayList<>();
		r.add(DlmsActionMsg.newBuilder().setRequestType(RequestType.GET_CONFIGURATION).build());
		return r;
	}
	
	
}
