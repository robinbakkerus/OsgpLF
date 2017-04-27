package org.osgp.smint.rest.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.osgp.dlms.MsgUtils;
import org.osgp.shared.exceptionhandling.TechnicalException;
import org.osgp.smint.SmIntAkkaServer;
import org.osgp.smint.dao.SmIntDao;
import org.osgp.smint.dao.SmIntDaoFact;
import org.osgp.smint.dao.SmIntTable;
import org.osgp.smint.service.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.HeDevOpSummMsg;
import com.alliander.osgp.dlms.HeJob;
import com.alliander.osgp.dlms.HeJobSumm;
import com.alliander.osgp.dlms.JobMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.ResponseMsg;
import com.alliander.osgp.shared.ResponseValuesMsg;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import akka.actor.ActorRef;

public class JobRestService extends AbstractService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobRestService.class.getName());
	
	private GetStatistics statisticsClient = null;
	
	public String getAllJobs() throws IOException {

		StringBuilder sb = startJsonArray();
		List<JobMsg> allMsg = dao().getAllJobs();
		int i=0;
		for (JobMsg job : allMsg) {
			addJson(makeHeJobSumm(job), sb);
			if (i++ < allMsg.size()-1) sb.append(",");
		}
		return endJsonArray(sb);
	}

	public String getJob(String jobId) {
		StringBuilder sb = startJson();
		JobMsg job = dao().getJob(Long.parseLong(jobId));
		if (job != null) {
			addJson(makeHeJob(job), sb);
		}
		return endJson(sb);
	}

	public String addJob(String data) {
//		System.out.println(data);
		try {
	        JobMsg.Builder builder = JobMsg.newBuilder();
			JsonFormat.parser().merge(data, builder);
			JobMsg msg = builder.build();
			dao().saveJob(msg);
			submitBundleRequest(msg);
			return "ok";
		} catch (InvalidProtocolBufferException e) {
			return "error : " + e;
		}
	}

	private static final SmIntTable[] SEARCH_TABLES = new SmIntTable[] {SmIntTable.RR_DONE, SmIntTable.RR_SEND, SmIntTable.RR_NEW};

	
	public String getResponse(final String correlId) {
		StringBuilder sb = startJson();
		RequestResponseMsg reqResp = null;
		for (SmIntTable table : SEARCH_TABLES) {
			reqResp = dao().getRequestResponse(correlId,table);
			if (reqResp != null) break;
		}

		if (reqResp != null) {
			try {
				addJson(fillResponseMsg(reqResp), sb);
			} catch (TechnicalException e) {
				sb.append(e.getMessage());
			}
		} 
		
		return endJson(sb);
	}
	
	private ResponseMsg fillResponseMsg(final RequestResponseMsg reqRespMsg) throws TechnicalException {
		List<ResponseValuesMsg> respValues = MsgUtils.getResponseValues(reqRespMsg);
		ResponseMsg r = ResponseMsg.newBuilder(reqRespMsg.getResponse()).addAllValues(respValues).build();
		return r;
	}
	
	private HeJobSumm makeHeJobSumm(final JobMsg job) {
		final String groupName = getDeviceGroupName(job.getDeviceGroupId());
		final String recipeName = getRecipeName(job.getRecipeId());
		
		return HeJobSumm.newBuilder().setId(job.getId()).setName(job.getName())
				.setCreationTime(job.getCreationTime())
				.setStatus(job.getStatus())
				.setDeviceGroup(groupName).setRecipe(recipeName)
				.setDevopsTotal(job.getDevopsTotal()).setDevopsSuccess(job.getDevopsSuccess()).setDevopsFailed(job.getDevopsFailed())
				.build();
	}

	private HeJob makeHeJob(final JobMsg job) {
		final String groupName = getDeviceGroupName(job.getDeviceGroupId());
		final String recipeName = getRecipeName(job.getRecipeId());

		return HeJob.newBuilder().setId(job.getId()).setName(job.getName())
				.setCreationTime(job.getCreationTime())
				.setStatus(job.getStatus())
				.setDevicegroupId(job.getDeviceGroupId()).setRecipeId(job.getRecipeId())
				.setDeviceGroup(groupName).setRecipe(recipeName)
				.setDevopsTotal(job.getDevopsTotal()).setDevopsSuccess(job.getDevopsSuccess()).setDevopsFailed(job.getDevopsFailed())
				.addAllDevops(makeHeDevOpSumm(job.getId()))
				.build();
	}

	private List<HeDevOpSummMsg> makeHeDevOpSumm(final long jobId) {
		List<HeDevOpSummMsg> r = new ArrayList<>();
		List<RequestResponseMsg> reqRespMsgsDone = dao().getRequestResponsesByJobId(jobId, SmIntTable.RR_DONE);
		List<RequestResponseMsg> reqRespMsgsSend = dao().getRequestResponsesByJobId(jobId, SmIntTable.RR_SEND);
		List<RequestResponseMsg> reqRespMsgsNew = dao().getRequestResponsesByJobId(jobId, SmIntTable.RR_NEW);
		reqRespMsgsDone.stream().limit(100).forEach(f -> r.add(makeHeDevOpSummMsg(f)));
		reqRespMsgsSend.stream().limit(100).forEach(f -> r.add(makeHeDevOpSummMsg(f)));
		reqRespMsgsNew.stream().limit(100).forEach(f -> r.add(makeHeDevOpSummMsg(f)));
		return r;
	}
	
	private HeDevOpSummMsg makeHeDevOpSummMsg(final RequestResponseMsg reqResp) {
		return HeDevOpSummMsg.newBuilder().setCorrelId(reqResp.getCorrelId())
				.setStatus(reqResp.getResponse().getStatus().name())
				.setDeviceId(reqResp.getCommon().getDeviceId()). build();
	}

	private void submitBundleRequest(final JobMsg jobMsg) {
		try {
			statisticsClient = GetStatistics.getInstance();
			statisticsClient.resetStatistics();
			ActorRef actor = SmIntAkkaServer.devsopsActor();
			actor.tell(jobMsg, ActorRef.noSender());
		} catch (Exception e) {
			LOGGER.error("error " + e);
		}		
	}
	
	private static Map<Long, String> sRecipeNamesMap = new java.util.HashMap<>();
	private static Map<Long, String> sDeviceGroupNamesMap = new java.util.HashMap<>();
	
	private String getRecipeName(final long recipeId) {
		if (sRecipeNamesMap.containsKey(recipeId)) {
			return sRecipeNamesMap.get(recipeId);
		} else {
			sRecipeNamesMap = dao().getAllrecipes().stream().collect(Collectors.toMap(r -> r.getId(), r -> r.getName()));
		}
		return sRecipeNamesMap.get(recipeId);
	}

	private String getDeviceGroupName(final long groupId) {
		if (sDeviceGroupNamesMap.containsKey(groupId)) {
			return sDeviceGroupNamesMap.get(groupId);
		} else {
			sDeviceGroupNamesMap = dao().getDeviceGroups().stream().collect(Collectors.toMap(r -> r.getId(), r -> r.getName()));
		}
		return sDeviceGroupNamesMap.get(groupId);
	}
	

	//---------
	
	private SmIntDao dao() {return SmIntDaoFact.INSTANCE.getDao();	}
	

}
