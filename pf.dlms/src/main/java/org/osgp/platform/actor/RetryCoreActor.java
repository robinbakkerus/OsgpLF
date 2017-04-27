package org.osgp.platform.actor;

import java.util.List;

import org.osgp.platform.dbs.PlatformDao;
import org.osgp.platform.dbs.PlatformDaoFact;
import org.osgp.platform.dbs.PlatformTable;
import org.osgp.platform.rpc.client.PfOsgpServiceCoreClient;
import org.osgp.platform.rpc.client.PfOsgpServiceCoreClientFact;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.UndeliveredTuple;
import org.osgp.util.AllMsgWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.RequestResponseMsg;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import io.grpc.StatusRuntimeException;

/**
 * Deze actor stuurt berichten opnieuw maar de Core laag, als die weer in de lucht i
 */
public class RetryCoreActor extends UntypedActor implements CC {

	private static final Logger LOGGER = LoggerFactory.getLogger(RetryCoreActor.class);

	public static final String RETRY = "retry";

	private ActorRef headActor;
	
	
	public RetryCoreActor(final ActorRef headActor) {
		super();
		this.headActor = headActor;
	}

	@Override
	public void onReceive(Object msg) {
		if (RETRY.equals(msg)) {
			doRetry();
		} else if (msg instanceof AllMsgWrapper) {
			saveAllMsg((AllMsgWrapper) msg);
		}
		context().stop(self());
	}

	private void doRetry() {
		List<UndeliveredTuple> list = dao().getAllUndeliveredRequests();
		if (!list.isEmpty()) {
			PfOsgpServiceCoreClient service = PfOsgpServiceCoreClientFact.client();
			if (service != null) {
				resendAllMessages(list, service);
			} else {
				scheduleRetryActor();
			}
		}
	}

	private void saveAllMsg(AllMsgWrapper allMsgWrapper) {
		for (RequestResponseMsg reqRespMsg : allMsgWrapper.getAllMsg()) {
			dao().saveUndeliveredRequest(reqRespMsg);
		}

		scheduleRetryActor();
	}

	private void scheduleRetryActor() {
		headActor.tell(PlatformHeadActor.SCHEDULE_CORE_RETRY, self());
	}

	private void resendAllMessages(List<UndeliveredTuple> list, PfOsgpServiceCoreClient service) {
		try {
			for (UndeliveredTuple tuple : list) {
				service.addNextRequest(tuple.getReqRespMsg());
			}
			service.setComplete();

			for (UndeliveredTuple tuple : list) {
				dao().delete(PlatformTable.CORE_UNDELIVERED, tuple.getPk());
			}

		} catch (StatusRuntimeException re) {
			LOGGER.error("error while retry sending msg to core ", re);
		}
	}

	private PlatformDao dao() {
		return PlatformDaoFact.INSTANCE.getDao();
	}

}
