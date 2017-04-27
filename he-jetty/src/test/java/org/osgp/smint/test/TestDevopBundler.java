package org.osgp.smint.test;

import java.security.cert.CertificateException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgp.smint.SmIntAkkaServer;
import org.osgp.smint.SmIntegrationMain;
import org.osgp.smint.dao.SmIntDbsMgr;
import org.osgp.smint.rpc.SmIntGrpcServer;
import org.osgp.smint.service.DevOpsBundler;
import org.osgp.smint.service.DevOpsSender;

import com.alliander.osgp.dlms.JobMsg;

import akka.actor.ActorSystem;

public class TestDevopBundler {

	private static ActorSystem actorSystem;
	private static SmIntAkkaServer akkaServer;

	@BeforeClass
	public static void before() {
		try {
			SmIntDbsMgr.INSTANCE.open();
			akkaServer = new SmIntAkkaServer();
//			grpcServer = new SmIntGrpcServer();
			actorSystem = akkaServer.startAkkaSystem(new String[] {});
//			grpcServer.start(actorSystem);
//			grpcServer.blockUntilShutdown();
			new GprcThread(actorSystem).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void after() {
		SmIntDbsMgr.INSTANCE.close();
	}
	
	@Test
	public void test() throws Exception {
		DevOpsBundler devopsBundler = new DevOpsBundler();
		//TODO JRB 
//		final JobMsg newJobMsg = devopsBundler.execute(makeJobMsg());
//		DevOpsSender devopsSender = new DevOpsSender(SmIntegrationMain.actorSystem());
//	    devopsSender.send(newJobMsg);
//	    Thread.sleep(100000);
	}

	private JobMsg makeJobMsg() {
		return JobMsg.newBuilder()
				.setId(1L)
				.setName("TestJob")
				.setDeviceGroupId(1L)
				.build();
	}
	
}

class GprcThread extends Thread {

	private final ActorSystem actorSystem;
	private	SmIntGrpcServer grpcServer;

	
    public GprcThread(ActorSystem actorSystem) {
		super();
		this.actorSystem = actorSystem;
	}

	public void run() {
		try {
			grpcServer = new SmIntGrpcServer();
			grpcServer.start(actorSystem);
			grpcServer.blockUntilShutdown();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
		} catch (Exception e) {
			e.printStackTrace();
		}
    }


}
