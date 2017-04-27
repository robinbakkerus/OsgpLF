package org.osgp.pa.dlms.dlms;

import org.osgp.pa.dlms.dlms.actor.SendResponsesActor;
import org.osgp.shared.CC;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class DlmsAkkaServer implements CC {

	public static ActorRef sendResponsesActor;
	
	ActorSystem startAkkaSystem(String[] args) {
	    ActorSystem system = ActorSystem.create(DLMS_SYSTEM, ConfigFactory.load(("dlms")));
	    sendResponsesActor = system.actorOf(Props.create(SendResponsesActor.class));
	    return system;
	}
}
