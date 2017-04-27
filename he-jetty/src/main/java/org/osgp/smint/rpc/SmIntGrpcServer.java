package org.osgp.smint.rpc;

import java.io.IOException;
import java.security.cert.CertificateException;

import org.osgp.util.rpc.GrpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import io.grpc.Server;
import io.netty.handler.ssl.SslProvider;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class SmIntGrpcServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(SmIntGrpcServer.class.getName());

	private static Config config = ConfigFactory.load("sm-int");

	private Server server;

	public void start(ActorSystem system) throws IOException, CertificateException {
		final int port = config.getInt("grpc.osgp-service.this.port");
		
		server = GrpcUtils.serverBuilder(port, "server1.pem", "server1.key", "ca.pem", SslProvider.OPENSSL)
				.addService(new SmIntNotificationServer())
				.addService(new SmIntOsgpServiceServer(system))
				.build().start();
		
		LOGGER.warn("Server started, listening on " + port);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.err.println("*** shutting down gRPC server since JVM is shutting down");
				SmIntGrpcServer.this.stop();
				System.err.println("*** server shut down");
			}
		});
	}

	public void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	/**
	 * Await termination on the main thread since the grpc library uses daemon
	 * threads.
	 */
	public void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}

}
