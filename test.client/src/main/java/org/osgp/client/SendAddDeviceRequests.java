package org.osgp.client;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.osgp.pa.dlms.util.DlmsDeviceMsgBuildHelper;
import org.osgp.shared.CC;
import org.osgp.util.rpc.GrpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.ActionMsg;
import com.alliander.osgp.shared.AddDeviceActionMsg;
import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.DeviceMsg;
import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.ProtocolSpecificMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

public class SendAddDeviceRequests {
	private static final Logger LOGGER = LoggerFactory.getLogger(SendAddDeviceRequests.class.getName());

	private ManagedChannel channel = null;
	private OsgpServiceGrpc.OsgpServiceStub osgpStub = null;

	/**
	 * Greet server. If provided, the first element of {@code args} is the name
	 * to use in the greeting.
	 */
	public static void main(String[] args) throws Exception {
		SendAddDeviceRequests client = new SendAddDeviceRequests("localhost",
				CC.GPRC_DLMSSRV_PLATFORM_PORT);
		try {
			client.addDevice();
		} finally {
			client.shutdown();
		}
	}

	public SendAddDeviceRequests(String host, int port) {
		try {
			ManagedChannel channel = GrpcUtils.makeChannel(port);
			osgpStub = OsgpServiceGrpc.newStub(channel);
		} catch (CertificateException | IOException e) {
			e.printStackTrace();
		}
	}

	public void addDevice() {
		final CountDownLatch finishLatch = new CountDownLatch(1);
		StreamObserver<AckMsg> respObserver = makeResponseObserver(finishLatch);
		StreamObserver<RequestResponseMsg> reqObserver = osgpStub.handleRequest(respObserver);
		try {
			final RequestResponseMsg reqRespMsg = makeReqRespMsg();
			reqObserver.onNext(reqRespMsg);
		} catch (RuntimeException e) {
			respObserver.onError(e);
			throw e;
		}
		reqObserver.onCompleted();
		finish(finishLatch);
	}

	private void finish(final CountDownLatch finishLatch) {
		try {
			finishLatch.await(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

	private RequestResponseMsg makeReqRespMsg() {
		final String devid = "NEW00001";
		DeviceMsg deviceMsg = DlmsDeviceMsgBuildHelper.makeDeviceMsg(devid);

		return RequestResponseMsg.newBuilder()
				.setCommon(makeCommon(devid))
				.setDevice(deviceMsg)
				.setAction(makeActionMsg(devid))
				.build();	
	}
	
	private ActionMsg makeActionMsg(final String devid) {
		return ActionMsg.newBuilder().setAddDevice(makeAddDeviceActionMsg(devid)).build();
	}

	private AddDeviceActionMsg makeAddDeviceActionMsg(final String devid) {
		return AddDeviceActionMsg.newBuilder().setProtocolSpecific(makeAddDeviceSpecific(devid)).build();
	}

	private ProtocolSpecificMsg makeAddDeviceSpecific(final String devid) {
		return ProtocolSpecificMsg.newBuilder().setRaw(DlmsDeviceMsgBuildHelper.makeDlmsDeviceMsg(devid).toByteString()).build();
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(50, TimeUnit.SECONDS);
	}

	private CommonMsg makeCommon(final String devid) {
		return CommonMsg.newBuilder().setApplicationName("Appname").setDeviceId(devid).setUserName("robinb")
				.setOrganisation(CC.INFOSTROOM).build();
	}

}
