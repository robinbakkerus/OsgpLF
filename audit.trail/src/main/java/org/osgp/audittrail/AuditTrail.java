package org.osgp.audittrail;

import java.io.IOException;

import org.osgp.audittrail.dao.AuditTrailDbsMgr;
import org.osgp.audittrail.rpc.AuditTrailGrpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class AuditTrail {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuditTrail.class.getName());

	private static AuditTrail auditTrail;
	private AuditTrailGrpcServer grpcServer = new AuditTrailGrpcServer();

	public static void main(String[] args) throws IOException, InterruptedException {
		try {
			auditTrail = new AuditTrail();
			LOGGER.info("Starting Audittrail server ...");
			AuditTrailDbsMgr.INSTANCE.open();
			auditTrail.grpcServer.start();
			auditTrail.grpcServer.blockUntilShutdown();
		} catch(Throwable t) {
			LOGGER.error(t.getMessage());
		} finally {
			AuditTrailDbsMgr.INSTANCE.close();
		}
	}


}
