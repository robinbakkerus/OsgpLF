package org.osgp.client.service;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.osgp.client.actor.DeleteBundledDevOpsSendActor;
import org.osgp.client.actor.DeleteDeviceOperationsWrapper;
import org.osgp.client.dao.ClientDao;
import org.osgp.client.dao.ClientDaoFact;
import org.osgp.shared.CC;
import org.osgp.util.rpc.GrpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

public class DevOpsSender implements CC {

	private static final Logger LOGGER = LoggerFactory.getLogger(DevOpsSender.class);

	private final ActorSystem system;
	private ManagedChannel channel = null;
	private OsgpServiceGrpc.OsgpServiceStub osgpStub = null;

	private static Config config = ConfigFactory.load("test-client");
	final int maxItemToSend = config.getInt("grpc-send-max-items");
	
	public DevOpsSender(ActorSystem system) {
		super();
		this.system = system;
	}

	public void send() {
		try {
			initialize();
			processAllDeviceOperations();
		} catch(InterruptedException ex) {
		} finally {
			shutdown();
		}
	}

	private void initialize() {
		try {
			ManagedChannel channel = GrpcUtils.makeChannel(CC.GPRC_DLMSSRV_PLATFORM_PORT);
			osgpStub = OsgpServiceGrpc.newStub(channel);
		} catch (CertificateException | IOException e) {
			e.printStackTrace();
		}
	}

	private void shutdown() {
		try {
			channel.shutdown().awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void processAllDeviceOperations() throws InterruptedException {
		List<RequestResponseMsg> allRequests = clientDao().getAllBundledDeviceOperations();
		int n = 0;
		List<RequestResponseMsg> requests = takeDeviceOperations(allRequests, n++);
		while (!requests.isEmpty()) {
			processDeviceOperations(requests);
			requests = takeDeviceOperations(allRequests, n++);
			System.out.println("... " + n);
		}
	}

	private void processDeviceOperations(List<RequestResponseMsg> requests) throws InterruptedException {
		final CountDownLatch finishLatch = new CountDownLatch(1);
		StreamObserver<AckMsg> respObserver = makeResponseObserver(finishLatch);
		StreamObserver<RequestResponseMsg> reqObserver = osgpStub.handleRequest(respObserver);
		try {
			for (RequestResponseMsg request : requests) {
				reqObserver.onNext(request);
			}
		} catch (RuntimeException e) {
			respObserver.onError(e);
			throw e;
		}
		reqObserver.onCompleted();
		deleteRequestsSend(requests);
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

			public void onError(Throwable t) {
				LOGGER.error(t.getMessage());
				finishLatch.countDown();
			}

			@Override
			public void onCompleted() {
				finishLatch.countDown();
			}
		};
	}
	
	private void deleteRequestsSend(List<RequestResponseMsg> requests) {
		ActorRef actor = system.actorOf(Props.create(DeleteBundledDevOpsSendActor.class));
		actor.tell(new DeleteDeviceOperationsWrapper(requests), ActorRef.noSender()); 
	}
	
	private ClientDao clientDao() {
		return ClientDaoFact.INSTANCE.getDao();
	}
	

}
