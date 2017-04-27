package org.osgp.platform.dbs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.osgp.shared.CC;
import org.osgp.shared.dbs.UndeliveredTuple;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.redis.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.CorrelIdMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;

import redis.clients.jedis.BinaryJedis;

public class PlatformDaoRedisImpl implements PlatformDao, CC {

	private static final Logger LOGGER = LoggerFactory.getLogger(PlatformDaoRedisImpl.class);
	
	@Override
	public void saveRequestResponse(RequestResponseMsg reqRespMsg) {
//		Bin bin2 = new Bin(CREATED_AT, now()); 
//		Bin bin3 = new Bin(MODIFIED_AT, "");
		jedis().set(reqRespKey(reqRespMsg), reqRespMsg.toByteArray());
	}

	@Override
	public void saveRequestResponses(List<RequestResponseMsg> reqRespMsgList) {
		reqRespMsgList.forEach(this::saveRequestResponse);
	}


	@Override
	public RequestResponseMsg getResponse(String correlId) throws InvalidProtocolBufferException {
		byte[] bytes = jedis().get(key(RK_PF_REQUEST_RESP, correlId));
		if (bytes != null) {
			return RequestResponseMsg.parseFrom((byte[]) bytes);
		} else {
			return null;
		}
	}

	@Override
	public RequestResponseMsg getResponse(CorrelIdMsg responseCorrelIdMsg) throws InvalidProtocolBufferException {
		return getResponse(responseCorrelIdMsg.getCorrelid());
	}
	
	@Override
	public void saveUndeliveredRequest(RequestResponseMsg reqRespMsg) {
		byte[] key = RedisUtils.key(RK_PF2CORE_UNDELIVERED, reqRespMsg.getCorrelId());
		jedis().set(key, reqRespMsg.toByteArray());
	}

	@Override
	public List<UndeliveredTuple> getAllUndeliveredRequests() {
		List<UndeliveredTuple> r = new ArrayList<>();
		Set<byte[]> keys = jedis().keys(scanKey(RK_PF2CORE_UNDELIVERED));
		for (byte[] key : keys) {
			try {
				RequestResponseMsg reqRespMsg = RequestResponseMsg.parseFrom(jedis().get(key));
				r.add(new UndeliveredTuple(new PK(key), reqRespMsg));
			} catch (InvalidProtocolBufferException e) {
				LOGGER.error("error parsing record " + e);
			}
		}
		return r;
	}
	
	@Override
	public List<RequestResponseMsg> getAllRequestResponseMsgs() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<PK> getAllRequestResponseMsgPKs() {
		List<PK> r = new ArrayList<>();
		Set<byte[]> keys = jedis().keys(scanKey(RK_PF_REQUEST_RESP));
		for (byte[] key : keys) {
			r.add(new PK(key));
		}
		return r;
	}

	@Override
	public void delete(PlatformTable table, PK pk) {
		jedis().del(pk.redisKey());
	}

	@Override
	public void deleteList(PlatformTable table, List<PK> pkList) {
		pkList.forEach(f -> this.delete(table, f));
	}

	
	@Override
	public Object get(PlatformTable table, PK pk) {
		// TODO Auto-generated method stub
		return null;
	}

	private byte[] key(final String prefix, final String keyValue) {
		return RedisUtils.key(prefix, keyValue);
	}

	private byte[] reqRespKey(final RequestResponseMsg reqRespMsg) {
		return RedisUtils.key(RK_PF_REQUEST_RESP, reqRespMsg.getCorrelId());
	}
	
	
	private byte[] scanKey(final String prefix) {
		return RedisUtils.scanKey(prefix);
	}

	private BinaryJedis jedis() {
		return PlatformDbsMgr.INSTANCE.dbsMgr().getRedisPool().jedis();
	}	
}
