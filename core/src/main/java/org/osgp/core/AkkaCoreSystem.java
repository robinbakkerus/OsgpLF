package org.osgp.core;

import java.util.concurrent.TimeUnit;

import org.osgp.core.actor.CoreHeadActor;
import org.osgp.core.actor.CoreRequestsHandlerActor;
import org.osgp.core.actor.CoreScheduleActor;
import org.osgp.core.actor.RescheduleActor;
import org.osgp.shared.CC;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class AkkaCoreSystem implements CC {

	private static Config config = ConfigFactory.load("core");
	
	private static ActorRef headActor;
	private static ActorRef coreScheduleActor;
	private static ActorRef rescheduleActor;
	private static ActorRef requestHandlerActor;

	static ActorSystem startAkkaSystem(String[] args) {
	    ActorSystem system = ActorSystem.create(CORE_SYSTEM, ConfigFactory.load(("core")));

	    headActor = system.actorOf(Props.create(CoreHeadActor.class), CoreHeadActor.NAME);
	    headActor.tell(CoreHeadActor.SCHEDULE_DLMS_RETRY, ActorRef.noSender());

	    coreScheduleActor = system.actorOf(Props.create(CoreScheduleActor.class));
		rescheduleActor = system.actorOf(Props.create(RescheduleActor.class));
		requestHandlerActor = system.actorOf(Props.create(CoreRequestsHandlerActor.class));

	    int waitFor = config.getInt("retry.schedule-wait");
		FiniteDuration duration = Duration.create(waitFor, TimeUnit.SECONDS);
		system.scheduler().schedule(duration,  duration, new Runnable() {
		    @Override
		    public void run() {
		    	coreScheduleActor.tell("run", ActorRef.noSender()); 
		    }
		}, system.dispatcher());
		
		
	    return system;
	}

	public static ActorRef headActor() {
		return headActor;
	}

	public static ActorRef coreScheduleActor() {
		return coreScheduleActor;
	}

	public static ActorRef rescheduleActor() {
		return rescheduleActor;
	}

	public static ActorRef requestHandlerActor() {
		return requestHandlerActor;
	}
	
	
	
	
	
}
