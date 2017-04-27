package org.osgp.core.actor;

import java.util.concurrent.TimeUnit;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class CoreHeadActor extends UntypedActor {

	public static final String NAME = CoreHeadActor.class.getSimpleName();
	public static final String SCHEDULE_DLMS_RETRY = "schedule-dlms-retry";
	
	private static Config config = ConfigFactory.load("core");

	@Override
	public void onReceive(Object msg) {
		if (SCHEDULE_DLMS_RETRY.equals(msg)) {
			scheduleCoreRetry();
		}
	}
	
	private void scheduleCoreRetry() {
		int waitFor = config.getInt("grpc.retry-dlms");
		FiniteDuration duration = Duration.create(waitFor, TimeUnit.SECONDS); 
		final ActorRef newActor = context().actorOf(Props.create(RetryDlmsActor.class, self()));
		context().system().scheduler().scheduleOnce(duration, new Runnable() {
		    @Override
		    public void run() {
		    	newActor.tell(RetryDlmsActor.RETRY, self());
		    }
		}, context().system().dispatcher());
	}

}
