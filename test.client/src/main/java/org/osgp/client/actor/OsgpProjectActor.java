package org.osgp.client.actor;

import java.io.IOException;

import org.osgp.audittrail.AuditTrail;
import org.osgp.core.Core;
import org.osgp.pa.dlms.dlms.Dlms;
import org.osgp.platform.Platform;

import akka.actor.UntypedActor;

public class OsgpProjectActor extends UntypedActor {

	public enum Project {
		PLATFORM,
		CORE,
		DLMS,
		AUDIT_TRAIL
	}
	
	@Override
	public void onReceive(Object msg) {
		if (msg instanceof Project) {
			try {
				startProject((Project) msg);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void startProject(Project project) throws IOException, InterruptedException {
		if (Project.PLATFORM == project) {
			Platform.main(new String[] {});
		} else if (Project.CORE == project) {
			Core.main(new String[] {});
		} else if (Project.DLMS == project) {
			Dlms.main(new String[] {});
		} else {
			AuditTrail.main(new String[] {});
		}
	}
}
