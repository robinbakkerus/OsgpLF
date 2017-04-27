package org.osgp.platform.rpc;

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
public class PlatformGrpcServers {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlatformGrpcServers.class.getName());

	private Config config = ConfigFactory.load("platform");

	private Server osgpServiceServer;
	
	public void start() throws IOException, CertificateException {
		final int osgpServicePort = config.getInt("grpc.osgp-service.this.port");

		osgpServiceServer = GrpcUtils.serverBuilder(osgpServicePort, "server1.pem", "server1.key", "ca.pem", SslProvider.OPENSSL)
				.addService(new OsgpServiceServerImpl())
				.build().start();
		
		LOGGER.warn("Server's started, listening on " + osgpServicePort);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.err.println("*** shutting down gRPC server since JVM is shutting down");
				PlatformGrpcServers.this.stop();
				System.err.println("*** server shut down");
			}
		});
	}

	public void stop() {
		if (osgpServiceServer != null) {
			osgpServiceServer.shutdown();
		}
	}

	/**
	 * Await termination on the main thread since the grpc library uses daemon
	 * threads.
	 */
	public void blockUntilShutdown() throws InterruptedException {
		if (osgpServiceServer != null) {
			osgpServiceServer.awaitTermination();
		}
	}

}
