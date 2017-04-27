package org.osgp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import org.osgp.shared.CC;
import org.osgp.util.rpc.GrpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.CorrelIdMsg;
import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.RequestResponseMsg;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

public class GetResponseMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetResponseMain.class.getName());

	private ManagedChannel channel;
	private OsgpServiceGrpc.OsgpServiceBlockingStub osgpStub;

	public static void main(String[] args) throws Exception {
		GetResponseMain client = new GetResponseMain("localhost", CC.GPRC_DLMSSRV_PLATFORM_PORT);
		try {
			String correlid = askCorrelId();
			while (!correlid.isEmpty()) {
				client.getResponse(correlid);
				correlid = askCorrelId();
			}
		} finally {
			client.shutdown();
		}
	}


	public GetResponseMain(String host, int port) {
		try {
			ManagedChannel channel = GrpcUtils.makeChannel(port);
			osgpStub = OsgpServiceGrpc.newBlockingStub(channel);
		} catch (CertificateException | IOException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(50, TimeUnit.SECONDS);
	}

	public void getResponse(final String correlid) {
		CorrelIdMsg correlIdMsg = CorrelIdMsg.newBuilder().setCorrelid(correlid).build();
		try {
			RequestResponseMsg response = osgpStub.getResponse(correlIdMsg);
			System.out.println(response);
			System.out.println("------");
		} catch (StatusRuntimeException e) {
			LOGGER.warn("RPC failed: {0}", e.getStatus());
		}
	}

	private static String askCorrelId() {
		System.out.println("Geef correlid: ");
		try{
		    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		    String s = bufferRead.readLine();
		    return s;
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	} 
}
