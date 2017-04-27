package org.osgp.client.test.dlms;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.io.IOException;
import java.security.cert.CertificateException;

import org.osgp.util.rpc.GrpcUtils;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.OsgpServiceGrpc.OsgpServiceImplBase;
import com.alliander.osgp.shared.RequestResponseMsg;

import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import io.netty.handler.ssl.SslProvider;

public class DlmsTestReplyHandler {
	
	private static final int PLATFORM_PORT = 50051;
	private static final int CORE_PORT = 50052;
	private static final int PORT = PLATFORM_PORT;
	private Server server;

	public void start() throws IOException, CertificateException {
		server = GrpcUtils.serverBuilder(PORT, "server1.pem", "server1.key", "ca.pem", SslProvider.OPENSSL)
				.addService(new ServiceImpl())
				.build().start();
		
		System.out.println("Server started, listening on " + PORT);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// Use stderr here since the logger may have been reset by its
				// JVM shutdown hook.
				System.err.println("*** shutting down gRPC server since JVM is shutting down");
				DlmsTestReplyHandler.this.stop();
				System.err.println("*** server shut down");
			}
		});
	}

	private void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	/**
	 * Await termination on the main thread since the grpc library uses daemon
	 * threads.
	 */
	private void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}

	public static void main(String[] args) throws Exception {
		final DlmsTestReplyHandler server = new DlmsTestReplyHandler();
		server.start();
		server.blockUntilShutdown();
	}

	static int cnt = 0;
	static long startTime = System.nanoTime();
	
	private class ServiceImpl extends OsgpServiceImplBase {

		@Override
		public StreamObserver<RequestResponseMsg> handleResponse(StreamObserver<AckMsg> responseObserver) {
			return new StreamObserver<RequestResponseMsg>() {
				@Override
				public void onNext(RequestResponseMsg arg0) {
					if (cnt % 25000 == 0) {
						long seconds = NANOSECONDS.toSeconds(System.nanoTime() - startTime);
						System.out.println("got response " + cnt + " in " + seconds) ;
					}
					cnt++;
				}

				@Override
				public void onCompleted() {
					AckMsg reply = AckMsg.newBuilder().build();
					responseObserver.onNext(reply);
					responseObserver.onCompleted();
				}

				@Override
				public void onError(Throwable arg0) {
					System.out.println("handleResponse cancelled");
				}
			};
		}

		@Override
		public void ping(AckMsg request, StreamObserver<AckMsg> responseObserver) {
			responseObserver.onNext(AckMsg.newBuilder().build());
			responseObserver.onCompleted();
		}

	}

}