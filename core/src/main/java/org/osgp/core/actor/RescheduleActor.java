package org.osgp.core.actor;

import org.osgp.core.dbs.CoreDao;
import org.osgp.core.dbs.CoreDaoFact;
import org.osgp.core.stats.CoreStatistics;
import org.osgp.core.utils.RescheduleHelper;
import org.osgp.shared.CC;

import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

import akka.actor.UntypedActor;

/**
 * Deze wordt aangeroepen door OsgpServiceServerImpl als dit bericht opnieuw gescheduled moet worden.
 * Eerst wordt de msg aangepast met de volgende schedule tijd, en dan opgeslagen in schedule repo.
 * De CoreScheduleActor zal op een gegeven moment deze msg dan weer naar de dlms sturen.
 */
public class RescheduleActor extends UntypedActor implements CC {

//	private static final Logger LOGGER = LoggerFactory.getLogger(RescheduleActor.class.getName());

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof RequestResponseMsg) {
			handleResponse((RequestResponseMsg) msg);
		}
	}

	private void handleResponse(RequestResponseMsg reqRespMsg) {
		CoreStatistics.incRetryCount();
		final RequestResponseMsg updatedReqRespMsg = incRetryCount(reqRespMsg);
		dao().saveReqRespToScheduleRepo(updatedReqRespMsg);
	}

	private RequestResponseMsg incRetryCount(RequestResponseMsg reqRespMsg) {
		int retrycnt = reqRespMsg.getCommon().getRetryCount() + 1;

		CommonMsg commonMsg = CommonMsg.newBuilder(reqRespMsg.getCommon())
				.setScheduleTime(RescheduleHelper.calcScheduleTime(reqRespMsg))
				.setRetryCount(retrycnt)
				.build();
		RequestResponseMsg result = RequestResponseMsg.newBuilder(reqRespMsg)
				.setCommon(commonMsg)
				.build();
		return result;
	}


	private CoreDao dao() {
		return CoreDaoFact.INSTANCE.getDao();
	}
}
