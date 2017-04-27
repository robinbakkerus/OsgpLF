package org.osgp.core;

import java.io.IOException;

import org.osgp.core.dbs.CoreDbsMgr;
import org.osgp.core.request.RequestHandlerFact;
import org.osgp.core.rpc.CoreGrpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorSystem;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class Core {
	private static final Logger LOGGER = LoggerFactory.getLogger(Core.class.getName());

	private CoreGrpcServer grpcServer = new CoreGrpcServer();
	private static ActorSystem actorSystem;

	/**
	 * Main launches the server from the command line.
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		try {
			LOGGER.info("Starting Core server");
			ProtocolHelper.initialize();
			RequestHandlerFact.initialize();
			CoreDbsMgr.INSTANCE.open();
			final Core mainServer = new Core();
			actorSystem = AkkaCoreSystem.startAkkaSystem(args);
			mainServer.grpcServer.start();
			mainServer.grpcServer.blockUntilShutdown();
		} catch(Exception ex) {
			LOGGER.error(ex.getMessage());
		} finally {
			CoreDbsMgr.INSTANCE.close();
		}
	}
	
	public static ActorSystem actorSystem() {
		return actorSystem;
	}
	
	
}
