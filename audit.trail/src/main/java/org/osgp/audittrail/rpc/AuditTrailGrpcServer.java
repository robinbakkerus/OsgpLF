package org.osgp.audittrail.rpc;

import java.io.IOException;
import java.security.cert.CertificateException;

import org.osgp.util.rpc.GrpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import io.grpc.Server;
import io.netty.handler.ssl.SslProvider;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class AuditTrailGrpcServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuditTrailGrpcServer.class.getName());

	private static Config config = ConfigFactory.load("audit-trail");
	
	private Server auditServiceServer;
	
	public AuditTrailGrpcServer() {
		super();
	}

	public void start() throws IOException, CertificateException {
		startAuditServiceServer();
	}

	private void startAuditServiceServer() throws IOException, CertificateException {
		final int port = config.getInt("grpc.audit-service.this.port");
		
		auditServiceServer = GrpcUtils.serverBuilder(port, "server1.pem", "server1.key", "ca.pem", SslProvider.OPENSSL)
				.addService(new AuditTrailServiceImpl())
				.build().start();

		LOGGER.warn("Server auditServiceServer started, listening on " + port);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.err.println("*** shutting down gRPC auditServiceServer since JVM is shutting down");
				AuditTrailGrpcServer.this.stop();
				System.err.println("*** server shut down");
			}
		});
	}

	public void stop() {
		
		if (auditServiceServer != null) {
			auditServiceServer.shutdown();
		}

	}

	/**
	 * Await termination on the main thread since the grpc library uses daemon
	 * threads.
	 */
	public void blockUntilShutdown() throws InterruptedException {
		
		if (auditServiceServer != null) {
			auditServiceServer.awaitTermination();
		}
	}

}
