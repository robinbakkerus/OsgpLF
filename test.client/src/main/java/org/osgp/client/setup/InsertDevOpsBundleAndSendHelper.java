package org.osgp.client.setup;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.osgp.client.service.DevOpsBundler;
import org.osgp.client.service.DevOpsGenerator;
import org.osgp.client.service.DevOpsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;

/**
 * Dit is programma waarmee requests naar osgp-lite worden verstuurd, om daar de
 * hele flow te kunnen testen. Deze maakt gebruikt van een door grpc
 * gegenereerde stub om protobuf msg naar het platform te sturen. Daar wordt die
 * PlatformReqMgsHandler opgevangen.
 */
public class InsertDevOpsBundleAndSendHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(InsertDevOpsBundleAndSendHelper.class);

	private ActorSystem system = ActorSystem.create("TEST_CLIENT", ConfigFactory.load(("test-client")));

	private DevOpsGenerator devopsGenerator = new DevOpsGenerator();
	private DevOpsBundler devopsBundler = new DevOpsBundler();
	private DevOpsSender devopsSender = new DevOpsSender(system);
	
	public static StringBuffer statistics = new StringBuffer();
			
	public static void main(String[] args) throws Exception {
		InsertDevOpsBundleAndSendHelper client = new InsertDevOpsBundleAndSendHelper();
		try {
			statistics = new StringBuffer();
			long time = now();
			int total = getTotal(args);
			client.doInsert(total);
			time = showLaptime(time, "Insert took ");
		    client.doBundle();
		    time = showLaptime(time, "Bundling took ");
		    client.doSend();
		    showLaptime(time, "Send took ");
		} catch (Throwable t) {
			LOGGER.error("error send rpc " + t);
		} finally {
		}
	}
	
	public void doInsert(int total) {
		devopsGenerator.doInsert(total);
	}

	public void doBundle() {
		devopsBundler.execute();
	}
	
	public void doSend() {
		devopsSender.send();
	}
	
	private static int getTotal(String[] args) {
		if (args != null && args.length > 0) {
			return Integer.parseInt(args[0]);
		} else {
			return askRequestCount();
		}
	}
	
	private static long now() {
		return System.nanoTime();
	}

	private static long showLaptime(long startedAt, String msg) {
		long seconds = NANOSECONDS.toMillis(System.nanoTime() - startedAt);
		System.err.println(msg + seconds + " msecs");
		statistics.append(msg + seconds + " msecs\n");
		return System.nanoTime();
	}
	
	private static int askRequestCount() {
		System.out.println("Voor hoeveel devices wil je inserten? : ");
		try {
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			String s = bufferRead.readLine();
			return new Integer(s).intValue();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

}
