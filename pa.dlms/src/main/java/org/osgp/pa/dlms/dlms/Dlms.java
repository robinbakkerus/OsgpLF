package org.osgp.pa.dlms.dlms;

import java.io.IOException;

import org.osgp.pa.dlms.application.dao.DlmsDbsMgr;
import org.osgp.pa.dlms.dlms.request.RequestHandlerFact;
import org.osgp.pa.dlms.rpc.DlmsGrpcServer;
import org.osgp.pa.dlms.rpc.OsgpServiceClientPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorSystem;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class Dlms {
	private static final Logger LOGGER = LoggerFactory.getLogger(Dlms.class);

	private DlmsAkkaServer akkaServer = new DlmsAkkaServer();
	private	DlmsGrpcServer grpcServer = new DlmsGrpcServer();
	
	private static ActorSystem actorSystem;

	/**
	 * Main launches the server from the command line.
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		LOGGER.info("Starting Protocol adapter Dlms server");
		try {
			final Dlms mainServer = new Dlms();
			DlmsDbsMgr.INSTANCE.open();
			actorSystem = mainServer.akkaServer.startAkkaSystem(args);
			OsgpServiceClientPool.initialize(actorSystem);
			RequestHandlerFact.initialize();
			mainServer.grpcServer.start(actorSystem);
			mainServer.grpcServer.blockUntilShutdown();
		} catch (Throwable t) {
			LOGGER.error(t.getMessage());
		} finally {
			DlmsDbsMgr.INSTANCE.close();
		}
	}

	public static ActorSystem actorSystem() {
		return actorSystem;
	}
	
}
