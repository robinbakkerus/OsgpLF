package org.osgp.smint;

import org.osgp.shared.CC;
import org.osgp.smint.actor.DevOpsActor;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class SmIntAkkaServer implements CC {

	public static ActorRef sendResponsesActor;
	
	private static ActorRef devopsActor;
	
	public ActorSystem startAkkaSystem(String[] args) {
	    ActorSystem system = ActorSystem.create("SM-INT", ConfigFactory.load(("sm-int")));
	    devopsActor = system.actorOf(Props.create(DevOpsActor.class));
	    return system;
	}
	
	public static ActorRef devsopsActor() {
		return devopsActor;
	}
}
