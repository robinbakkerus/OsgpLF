package org.osgp.smint.dao;

import java.util.List;

import org.osgp.shared.CC;
import org.osgp.util.dao.PK;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Record;
import com.aerospike.client.policy.Policy;
import com.alliander.osgp.dlms.DeviceGroupMsg;
import com.alliander.osgp.dlms.JobMsg;
import com.alliander.osgp.dlms.RecipeMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

public class SmIntDaoImplAerospike implements SmIntDao, CC {

//	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SmIntDaoImplAerospike.class);

	// --- delete


	@Override
	public void delete(final SmIntTable table, final PK pk) {
		dbsClient().delete(null, pk.aeroKey());
	}

	@Override
	public void deleteList(final SmIntTable table, List<PK> pkList) {
		pkList.forEach(f -> this.delete(table, f));
	}
	
	//---------------------
	

	@Override
	public Record get(final SmIntTable table, final PK pk) {
		return dbsClient().get(new Policy(), pk.aeroKey());
	}

	// ======================================

	private AerospikeClient dbsClient() {
		return SmIntDbsMgr.INSTANCE.dbsMgr().getAeroServer().getClient();
	}


	//----------- HeGui ---------------
	
	@Override
	public List<JobMsg> getAllJobs() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void saveJob(JobMsg job) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveRecipe(RecipeMsg recipe) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<RecipeMsg> getAllrecipes() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public JobMsg getJob(long id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public RecipeMsg getRecipe(final long id) {
		// TODO Auto-generated method stub
		return null;
	}

	// RequestResponseMsg
	@Override
	public void saveRequestResponse(RequestResponseMsg reqRespMsg, SmIntTable table) {
		// TODO Auto-generated method stub
	}

	@Override
	public void saveRequestResponses(List<RequestResponseMsg> reqRespMsgList, SmIntTable table) {
		reqRespMsgList.forEach(f -> this.saveRequestResponse(f, table));
	}

	@Override
	public List<RequestResponseMsg> getAllRequestResponses(SmIntTable table) {
		return null;
	}


	@Override
	public RequestResponseMsg getRequestResponse(String correlId, SmIntTable table) {
		return null;
	}
	
	@Override
	public List<RequestResponseMsg> getRequestResponsesByJobId(long jobId, SmIntTable table) {
		return null;
	}


	//--- DeviceGroup
	
	@Override
	public void saveDeviceGroup(DeviceGroupMsg deviceGroupMsg) {
	}


	@Override
	public List<DeviceGroupMsg> getDeviceGroups() {
		return null;
	}	

	@Override
	public DeviceGroupMsg getDeviceGroup(final long id) {
		return null;
	}	

}
