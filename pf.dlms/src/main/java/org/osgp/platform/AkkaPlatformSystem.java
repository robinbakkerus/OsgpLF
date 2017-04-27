package org.osgp.platform;

import org.osgp.platform.actor.PlatformHeadActor;
import org.osgp.platform.actor.PlatformRequestHandlerActor;
import org.osgp.platform.actor.PlatformResponseHandlerActor;
import org.osgp.shared.CC;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class AkkaPlatformSystem implements CC {
	
	private static ActorSystem actorSystem;
	private static ActorRef headActor;
	private static ActorRef requestHandlerActor;
	private static ActorRef responseHandlerActor;
	
	private AkkaPlatformSystem() {
	}

	static void startAkkaSystem() {
		actorSystem = ActorSystem.create(PLATFORM_SYSTEM, ConfigFactory.load(("platform")));
	    headActor = actorSystem.actorOf(Props.create(PlatformHeadActor.class), PlatformHeadActor.NAME);
	    headActor.tell(PlatformHeadActor.SCHEDULE_CORE_RETRY, ActorRef.noSender());
	    requestHandlerActor = actorSystem.actorOf(Props.create(PlatformRequestHandlerActor.class), PlatformRequestHandlerActor.NAME);
	    responseHandlerActor = actorSystem.actorOf(Props.create(PlatformResponseHandlerActor.class), PlatformResponseHandlerActor.NAME);
	}
	
	public static ActorSystem actorSystem() {
		return actorSystem;
	}
	
	public static ActorRef headActor() {
		return headActor;
	}

	public static ActorRef requestHandlerActor() {
		return requestHandlerActor;
	}
	
	public static ActorRef responseHandlerActor() {
		return responseHandlerActor;
	}
}
