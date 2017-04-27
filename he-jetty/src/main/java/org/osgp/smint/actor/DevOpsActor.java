package org.osgp.smint.actor;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.osgp.shared.exceptionhandling.ComponentType;
import org.osgp.shared.exceptionhandling.TechnicalException;
import org.osgp.smint.dao.SmIntDao;
import org.osgp.smint.dao.SmIntDaoFact;
import org.osgp.smint.dao.SmIntTable;
import org.osgp.smint.service.DevOpsBundler;
import org.osgp.smint.service.DevOpsSender;
import org.osgp.util.dao.PK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DeviceGroupMsg;
import com.alliander.osgp.dlms.JobMsg;
import com.alliander.osgp.shared.RequestResponseListMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.ResponseStatus;

import akka.actor.UntypedActor;
import io.grpc.StatusRuntimeException;

/*
 * this actor saves/moves the ResponseRequestMsg in resp the new/send/done table. 
 *
 */
public class DevOpsActor extends UntypedActor {

	private static final Logger LOGGER = LoggerFactory.getLogger(DevOpsActor.class);
	
	private DevOpsBundler devopsBundler = new DevOpsBundler();
	private DevOpsSender devopsSender = new DevOpsSender();

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof JobMsg) {
			handleNewJob((JobMsg) msg);
		} else if (msg instanceof RequestResponseListMsg) {
			moveFromSendToDone((RequestResponseListMsg) msg);
		}
	}

	private void handleNewJob(final JobMsg jobMsg) {
		try {
		long time = System.nanoTime();
			List<String> deviceIds = getDeviceIds(jobMsg);
			final List<RequestResponseMsg> reqRespMsgs = devopsBundler.makeRequestResponses(jobMsg, deviceIds);
			dao().saveRequestResponses(reqRespMsgs, SmIntTable.RR_SEND);
			this.saveJob(jobMsg, deviceIds.size());
			time = showLaptime(time, "Bundling took ");
			devopsSender.send(reqRespMsgs);
			showLaptime(time, "Send took ");
		} catch (Exception e) {
			LOGGER.error("error " + e, e);
		}
	}

//	private void moveFromNewToSend(List<RequestResponseMsg> reqRespMsgs) {
//		reqRespMsgs.forEach(f -> dao().saveRequestResponse(f, SmIntTable.RR_SEND));
//		reqRespMsgs.forEach(f -> deleteFromNew(f));
//	}

//	private void deleteFromNew(final RequestResponseMsg reqResp) {
//		PK pk = new PK(reqResp.getCorrelId());
//		dao().delete(SmIntTable.RR_NEW, pk);
//	}

	private void moveFromSendToDone(RequestResponseListMsg msg) {
		final List<RequestResponseMsg> allReqResp = msg.getRequestResponsesList();
		dao().saveRequestResponses(allReqResp, SmIntTable.RR_DONE);
		List<PK> allPks = allReqResp.stream().map(f -> new PK(f.getCorrelId(), SmIntTable.RR_SEND.getTableName())).collect(Collectors.toList());
		dao().deleteList(SmIntTable.RR_SEND, allPks);
		updateJobTotals(allReqResp);
	}
	
	private void updateJobTotals(final List<RequestResponseMsg> reqRespMsgs) {
		Map<Long, List<RequestResponseMsg>> jobReqResponsesMap = new HashMap<>();
		reqRespMsgs.forEach(r -> updateJobTotalPrepareMap(r, jobReqResponsesMap));
		jobReqResponsesMap.keySet().forEach(jobId -> updateJobTotals(jobId, jobReqResponsesMap.get(jobId)));
	}
	
	private void updateJobTotalPrepareMap(final RequestResponseMsg reqRespMsg, Map<Long, List<RequestResponseMsg>> jobReqResponsesMap ) {
		final Long jobId =reqRespMsg.getCommon().getJobId();
		List<RequestResponseMsg> reqRespList = jobReqResponsesMap.get(jobId);
		if (reqRespList == null) {
			reqRespList = new ArrayList<>();
			reqRespList.add(reqRespMsg);
			jobReqResponsesMap.put(jobId, reqRespList);
		} else {
			reqRespList.add(reqRespMsg);
		}
	}
	
	
	private void updateJobTotals(final long jobId, final List<RequestResponseMsg> reqRespMsgs) {
		JobMsg jobMsg = dao().getJob(jobId);
		if (jobMsg != null) {
			Map<ResponseStatus, Long> counters = reqRespMsgs.stream().collect(groupingBy(r -> r.getResponse().getStatus(), counting()));
			Long succesCnt = jobMsg.getDevopsSuccess() + count(counters, ResponseStatus.OK);
			Long failedCnt = jobMsg.getDevopsFailed() + count(counters, ResponseStatus.NOT_OK) + count(counters, ResponseStatus.NOT_SET);
			JobMsg newJobMsg = JobMsg.newBuilder(jobMsg).setDevopsSuccess(succesCnt.intValue()).setDevopsFailed(failedCnt.intValue()).build();
			dao().saveJob(newJobMsg);
		} else {
			LOGGER.error("no job found fort {}",jobId);
		}
	}
	
	private long count(final Map<ResponseStatus, Long> counters, final ResponseStatus status) {
		return counters.get(status)==null ? 0L : counters.get(status);
	}

//	private void deleteFromSend(final RequestResponseMsg reqResp) {
//		PK pk = new PK(reqResp.getCorrelId());
//		dao().delete(SmIntTable.RR_SEND, pk);
//	}

	private JobMsg saveJob(JobMsg jobMsg, final int total) throws TechnicalException {
		try {
			final JobMsg newJobMsg = JobMsg.newBuilder(jobMsg).setCreationTime(now()).setDevopsTotal(total)
					.build();
			dao().saveJob(newJobMsg);
			return newJobMsg;
		} catch (StatusRuntimeException e) {
			LOGGER.warn("RPC failed: {0}", e.getStatus());
			throw new TechnicalException(ComponentType.DOMAIN_SMART_METERING, e);
		}
	}

	private List<String> getDeviceIds(final JobMsg jobMsg) {
		DeviceGroupMsg deviceGrp = dao().getDeviceGroup(jobMsg.getDeviceGroupId());
		if (deviceGrp != null) {
			return deviceGrp.getDeviceIdsList();
		} else {
			return new ArrayList<>();
		}
	}

	// ----
	private long now() {return new Date().getTime();}
	private SmIntDao dao() {return SmIntDaoFact.INSTANCE.getDao();}

	private long showLaptime(long startedAt, String msg) {
		long seconds = NANOSECONDS.toMillis(System.nanoTime() - startedAt);
		System.out.println(msg + seconds + " msecs");
		return System.nanoTime();
	}
}

