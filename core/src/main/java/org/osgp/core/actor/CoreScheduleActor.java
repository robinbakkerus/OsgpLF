package org.osgp.core.actor;

import java.util.Date;
import java.util.List;

import org.osgp.core.dbs.CoreDao;
import org.osgp.core.dbs.CoreDaoFact;
import org.osgp.core.dbs.ScheduledTaskTuple;
import org.osgp.core.rpc.OsgpServicePAClient;
import org.osgp.core.rpc.OsgpServicePAClientFact;
import org.osgp.core.stats.CoreStatistics;
import org.osgp.shared.CC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.UntypedActor;

public class CoreScheduleActor extends UntypedActor implements CC {

	private static final Logger LOGGER = LoggerFactory.getLogger(CoreScheduleActor.class.getName());

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof String) {
			procesScheduledMsgs();
		}
	}

	private void procesScheduledMsgs() {
		LOGGER.debug("CoreScheduleActor called at " + new Date());
		List<ScheduledTaskTuple> tasks = dao().getAllScheduledTasks();

		if (tasks.size() > 0) {
			final OsgpServicePAClient osgpService = OsgpServicePAClientFact.client();
			for (ScheduledTaskTuple tuple : tasks) {
				osgpService.addNextRequest(tuple.getMsg());
				CoreStatistics.incRequestsOut();
				dao().delete(tuple.getPk());
			}
			osgpService.setComplete();
		}
	}

	
	private CoreDao dao() {
		return CoreDaoFact.INSTANCE.getDao();
	}
}
