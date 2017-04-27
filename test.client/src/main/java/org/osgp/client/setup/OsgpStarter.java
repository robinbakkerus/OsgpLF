package org.osgp.client.setup;

import org.osgp.client.actor.OsgpProjectActor;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class OsgpStarter {

	private ActorSystem system = ActorSystem.create("TEST_CLIENT", ConfigFactory.load(("test-client")));
	
	private ActorRef platform;
	private ActorRef core;
	private ActorRef dlms;
	@SuppressWarnings("unused")
	private ActorRef auditTrail;
	
	public void startAll() {
		platform = system.actorOf(Props.create(OsgpProjectActor.class), "platform");
		core = system.actorOf(Props.create(OsgpProjectActor.class), "core");
		dlms = system.actorOf(Props.create(OsgpProjectActor.class), "dlms");
		auditTrail = system.actorOf(Props.create(OsgpProjectActor.class), "auditTrail");
		
		platform.tell(OsgpProjectActor.Project.PLATFORM, ActorRef.noSender());
		core.tell(OsgpProjectActor.Project.CORE, ActorRef.noSender());
		dlms.tell(OsgpProjectActor.Project.DLMS, ActorRef.noSender());
//		auditTrail.tell(OsgpProjectActor.Project.AUDIT_TRAIL, ActorRef.noSender());
		
	}
}
