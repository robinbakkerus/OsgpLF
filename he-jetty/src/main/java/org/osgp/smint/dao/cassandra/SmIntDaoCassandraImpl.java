package org.osgp.smint.dao.cassandra;

import static org.osgp.util.dao.cassandra.CassandraHelper.blob;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.osgp.smint.dao.SmIntDao;
import org.osgp.smint.dao.SmIntDbsMgr;
import org.osgp.smint.dao.SmIntTable;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.cassandra.CassandraHelper;

import com.alliander.osgp.dlms.DeviceGroupMsg;
import com.alliander.osgp.dlms.JobMsg;
import com.alliander.osgp.dlms.RecipeMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.datastax.driver.core.Session;

public class SmIntDaoCassandraImpl implements SmIntDao {

	final CassandraHelper<RequestResponseMsg> rrNewHelper = 
			new CassandraHelper<>(SmIntCassandraClient.getTableData(SmIntTable.RR_NEW.getTableName()));
	final CassandraHelper<RequestResponseMsg> rrSendHelper = 
			new CassandraHelper<>(SmIntCassandraClient.getTableData(SmIntTable.RR_SEND.getTableName()));
	final CassandraHelper<RequestResponseMsg> rrDoneHelper = 
			new CassandraHelper<>(SmIntCassandraClient.getTableData(SmIntTable.RR_DONE.getTableName()));
	final CassandraHelper<JobMsg> jobHelper = 
			new CassandraHelper<>(SmIntCassandraClient.getTableData(SmIntTable.JOB.getTableName()));
	final CassandraHelper<DeviceGroupMsg> groupHelper = 
			new CassandraHelper<>(SmIntCassandraClient.getTableData(SmIntTable.DEV_GROUP.getTableName()));
	final CassandraHelper<RecipeMsg> recipeHelper = 
			new CassandraHelper<>(SmIntCassandraClient.getTableData(SmIntTable.RECIPE.getTableName()));

	@Override
	public void delete(SmIntTable table, PK pk) {
		helper(table).delete(session(), pk);
	}

	
	@Override
	public void deleteList(SmIntTable table, List<PK> pkList) {
		helper(table).deleteList(session(), pkList);
	}


	@Override
	public Object get(SmIntTable table, PK pk) {
		return null;
	}

	@Override
	public void saveJob(JobMsg job) {
		jobHelper.save(session(), job.getId(), blob(job));
	}

	@Override
	public List<JobMsg> getAllJobs() {
		return jobHelper.selectAll(session());
	}

	@Override
	public JobMsg getJob(long id) {
		return jobHelper.select(session(), id);
	}

	@Override
	public void saveRecipe(RecipeMsg recipe) {
		recipeHelper.save(session(), recipe.getId(), blob(recipe));
	}

	@Override
	public List<RecipeMsg> getAllrecipes() {
		return recipeHelper.selectAll(session());
	}

	@Override
	public RecipeMsg getRecipe(long recipeId) {
		return recipeHelper.select(session(), recipeId);
	}

	@Override
	public void saveRequestResponse(RequestResponseMsg reqRespMsg, SmIntTable table) {
		this.helper(table).save(session(), reqRespMsg.getCorrelId(), blob(reqRespMsg));
	}

	
	@Override
	public void saveRequestResponses(List<RequestResponseMsg> reqRespMsgList, SmIntTable table) {
		List<List<Object>> valuesList = new ArrayList<>();
		reqRespMsgList.stream().forEach(f -> valuesList.add(this.makeRequestResponseMsgList(f)));
		this.helper(table).saveList(session(), valuesList);
	}

	private List<Object> makeRequestResponseMsgList(final RequestResponseMsg msg) {
		List<Object> r = new ArrayList<>();
		r.add(msg.getCorrelId());
		r.add(blob(msg));
		return r;
	}
	@Override
	public List<RequestResponseMsg> getAllRequestResponses(SmIntTable table) {
		return helper(table).selectAll(session());
	}

	@Override
	public RequestResponseMsg getRequestResponse(String correlId, SmIntTable table) {
		return helper(table).select(session(), correlId);
	}

	@Override
	public List<RequestResponseMsg> getRequestResponsesByJobId(long jobId, SmIntTable table) {
		List<RequestResponseMsg> allReqResp = getAllRequestResponses(table);
		return allReqResp.stream().filter(r -> r.getCommon().getJobId()==jobId).collect(Collectors.toList());
	}

	@Override
	public void saveDeviceGroup(DeviceGroupMsg deviceGroupMsg) {
		groupHelper.save(session(), deviceGroupMsg.getId(), blob(deviceGroupMsg));
	}

	@Override
	public List<DeviceGroupMsg> getDeviceGroups() {
		return groupHelper.selectAll(session());
	}

	@Override
	public DeviceGroupMsg getDeviceGroup(long groupId) {
		return groupHelper.select(session(), groupId);
	}

	// --------------

	private static Session session() {
		return SmIntDbsMgr.INSTANCE.dbsMgr().getCassandraSession();
	}
	
	private CassandraHelper<RequestResponseMsg> helper(final  SmIntTable table) {
		if (SmIntTable.RR_NEW == table) {
			return rrNewHelper;
		} else if (SmIntTable.RR_SEND == table) {
			return rrSendHelper;
		} else {
			return rrDoneHelper;
		}
	}
	

}
