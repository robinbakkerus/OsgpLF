package org.osgp.smint.dao;

import java.util.List;

import org.osgp.shared.CC;
import org.osgp.util.dao.PK;

import com.alliander.osgp.dlms.DeviceGroupMsg;
import com.alliander.osgp.dlms.JobMsg;
import com.alliander.osgp.dlms.RecipeMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

import redis.clients.jedis.BinaryJedis;

public class SmIntDaoRedisImpl implements SmIntDao, CC {

//	private static final Logger LOGGER = LoggerFactory.getLogger(SmIntDaoRedisImpl.class);


	@Override
	public byte[] get(final SmIntTable table, final PK pk) {
		return jedis().get(pk.redisKey());
	}
	
	@Override
	public void delete(final SmIntTable table, final PK pk) {
		jedis().del(pk.redisKey());
	}

	@Override
	public void deleteList(final SmIntTable table, List<PK> pkList) {
		pkList.forEach(f -> this.delete(table, f));
	}
	
//	private byte[] key(final String prefix, final String keyValue) {
//		return RedisUtils.key(prefix, keyValue);
//	}
//
//	private byte[] scanKey(final String prefix) {
//		return RedisUtils.scanKey(prefix);
//	}
//
//	private String makeUniqueId() {
//		return UUID.randomUUID().toString();
//	}

	private static BinaryJedis jedis() {
		return SmIntDbsMgr.INSTANCE.dbsMgr().getRedisPool().jedis();
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
		reqRespMsgList.forEach(f -> saveRequestResponse(f, table));
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
