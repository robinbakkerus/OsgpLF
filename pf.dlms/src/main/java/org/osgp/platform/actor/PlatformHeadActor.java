package org.osgp.platform.actor;

import java.util.concurrent.TimeUnit;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class PlatformHeadActor extends UntypedActor {

	public static final String NAME = PlatformHeadActor.class.getSimpleName();
	public static final String SCHEDULE_CORE_RETRY = "schedule-core-retry";
	
	private static Config config = ConfigFactory.load("platform");

	@Override
	public void onReceive(Object msg) {
		if (SCHEDULE_CORE_RETRY.equals(msg)) {
			scheduleCoreRetry();
		}
	}
	
	private void scheduleCoreRetry() {
		int waitFor = config.getInt("grpc.retry-core");
		FiniteDuration duration = Duration.create(waitFor, TimeUnit.SECONDS); 
		final ActorRef newActor = context().actorOf(Props.create(RetryCoreActor.class, self()));
		context().system().scheduler().scheduleOnce(duration, new Runnable() {
		    @Override
		    public void run() {
		    	newActor.tell(RetryCoreActor.RETRY, self());
		    }
		}, context().system().dispatcher());
	}

}
