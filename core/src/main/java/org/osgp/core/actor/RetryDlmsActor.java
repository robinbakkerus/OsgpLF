package org.osgp.core.actor;

import java.util.List;

import org.osgp.core.dbs.CoreDao;
import org.osgp.core.dbs.CoreDaoFact;
import org.osgp.core.rpc.OsgpServicePAClient;
import org.osgp.core.rpc.OsgpServicePAClientFact;
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
  * Deze actor stuurt berichten opnieuw maar de protocol adapter laag, als die weer in de lucht i

 */
public class RetryDlmsActor extends UntypedActor implements CC {

	private static final Logger LOGGER = LoggerFactory.getLogger(RetryDlmsActor.class);

	public static final String RETRY = "retry";

	private ActorRef headActor;
	
	public RetryDlmsActor(ActorRef headActor) {
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
			OsgpServicePAClient service = OsgpServicePAClientFact.client();
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
		headActor.tell(CoreHeadActor.SCHEDULE_DLMS_RETRY, self());
	}

	private void resendAllMessages(List<UndeliveredTuple> list, OsgpServicePAClient service) {
		try {
			for (UndeliveredTuple tuple : list) {
				service.addNextRequest(tuple.getReqRespMsg());
			}
			service.setComplete();

			for (UndeliveredTuple tuple : list) {
				dao().delete(tuple.getPk());
			}

		} catch (StatusRuntimeException re) {
			LOGGER.error("error while retry sending msg to core ", re);
		}
	}

	private CoreDao dao() {
		return CoreDaoFact.INSTANCE.getDao();
	}

}
