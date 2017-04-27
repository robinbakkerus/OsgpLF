package org.osgp.audittrail.dao;

import java.util.List;

import org.osgp.util.dao.PK;

import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;

import redis.clients.jedis.BinaryJedis;

public class AuditTrailRedisImpl implements AuditTrailDao {

	@Override
	public void saveRequestResponseMsg(RequestResponseMsg reqRespMsg) {
		String correlId = reqRespMsg.getCorrelId();
		getJedis().set(correlId.getBytes(), reqRespMsg.toByteArray());
	}

	@Override
	public RequestResponseMsg getRequestResponseMsg(String correlId) {
		Object o = getJedis().get(correlId.getBytes());
		try {
			if (o != null) {
				return RequestResponseMsg.parseFrom((byte[]) o);
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void delete(PK pk) {
		// TODO Auto-generated method stub
	}

	
	@Override
	public List<PK> getAllRequestResponseMsgPKs() {
		return null;
	}

	private static BinaryJedis getJedis() {
		return AuditTrailDbsMgr.INSTANCE.dbsMgr().getRedisPool().jedis();
	}
}
