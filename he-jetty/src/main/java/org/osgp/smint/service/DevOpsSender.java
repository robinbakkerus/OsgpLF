package org.osgp.smint.service;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.osgp.shared.CC;
import org.osgp.util.rpc.GrpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

public class DevOpsSender implements CC {

	private static final Logger LOGGER = LoggerFactory.getLogger(DevOpsSender.class);

	private ManagedChannel channel = null;
	private OsgpServiceGrpc.OsgpServiceStub osgpStub = null;

	private static Config config = ConfigFactory.load("sm-int");
	final int maxItemToSend = config.getInt("grpc-send-max-items");
	
	public DevOpsSender() {
		super();
	}

	public void send(final List<RequestResponseMsg> reqRespMsgs) throws InterruptedException {
		try {
			initialize();
			processAllDeviceOperations(reqRespMsgs);
		} finally {
			shutdown();
		}
	}

	private void initialize() {
		try {
			channel = GrpcUtils.makeChannel(CC.GPRC_DLMSSRV_PLATFORM_PORT);
			osgpStub = OsgpServiceGrpc.newStub(channel);
		} catch (CertificateException | IOException e) {
			LOGGER.error("error "  + e, e);
		}
	}

	private void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(1, TimeUnit.MINUTES);
	}

	private void processAllDeviceOperations(final List<RequestResponseMsg> reqRespMsgs) throws InterruptedException {
		int n = 0;
		List<RequestResponseMsg> requests = takeDeviceOperations(reqRespMsgs, n++);
		while (!requests.isEmpty()) {
			processDeviceOperations(requests);
			requests = takeDeviceOperations(reqRespMsgs, n++);
			System.out.println("... " + n);
		}
	}

	private void processDeviceOperations(final List<RequestResponseMsg> requests) throws InterruptedException {
		final CountDownLatch finishLatch = new CountDownLatch(1);
		StreamObserver<AckMsg> respObserver = makeResponseObserver(finishLatch);
		StreamObserver<RequestResponseMsg> reqObserver = osgpStub.handleRequest(respObserver);
		try {
			requests.forEach(r -> reqObserver.onNext(r));
		} catch (RuntimeException e) {
			respObserver.onError(e);
			throw e;
		}
		reqObserver.onCompleted();
		finishLatch.await(1, TimeUnit.MINUTES);
	}

	private List<RequestResponseMsg> takeDeviceOperations(List<RequestResponseMsg> allRequests,int loop) {
		int max = maxItemToSend;
		return  IntStream.range(0, allRequests.size())
			    .filter(n -> n >= (loop*max) && n < (loop+1)*max)
			    .mapToObj(allRequests::get)
			    .collect(Collectors.toList());
	}
	

	private StreamObserver<AckMsg> makeResponseObserver(CountDownLatch finishLatch) {
		return new StreamObserver<AckMsg>() {
			@Override
			public void onNext(AckMsg ack) {
				LOGGER.info(ack.getStatus());
			}

			@Override
			public void onError(Throwable t) {
				LOGGER.error(t.getMessage(), t);
				finishLatch.countDown();
			}

			@Override
			public void onCompleted() {
				finishLatch.countDown();
			}
		};
	}

}
