package org.osgp.smint;

import org.osgp.smint.dao.SmIntDbsMgr;
import org.osgp.smint.rest.JobController;
import org.osgp.smint.rest.service.DeviceGroupRestService;
import org.osgp.smint.rest.service.JobRestService;
import org.osgp.smint.rest.service.RecipeRestService;
import org.osgp.smint.rest.service.StatisticsRestService;
import org.osgp.smint.rpc.SmIntGrpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorSystem;

public class SmIntegrationMain {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SmIntegrationMain.class);

	private static ActorSystem actorSystem;
	private SmIntAkkaServer akkaServer = new SmIntAkkaServer();
	private	SmIntGrpcServer grpcServer = new SmIntGrpcServer();
	
	public static void main(String[] args) {
		try {
			registerShutdownHook();
			SmIntegrationMain mainServer = new SmIntegrationMain();
			SmIntDbsMgr.INSTANCE.open();
			new JobController(new JobRestService(), new RecipeRestService(), new DeviceGroupRestService(), 
					new StatisticsRestService());
			actorSystem = mainServer.akkaServer.startAkkaSystem(args);
			mainServer.grpcServer.start(actorSystem);
			mainServer.grpcServer.blockUntilShutdown();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			System.out.println("Finish up application.");
			SmIntDbsMgr.INSTANCE.close();
		}
	}

	public static ActorSystem actorSystem() {
		return actorSystem;
	}
	
	private static void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    @Override
		    public void run() {
		    	System.out.println("Shutdown hook triggered, application is shutting down...");
		    	SmIntDbsMgr.INSTANCE.close();
		    }
		});

	}
}
