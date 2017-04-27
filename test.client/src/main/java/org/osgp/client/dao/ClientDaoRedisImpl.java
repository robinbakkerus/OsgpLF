package org.osgp.client.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.osgp.shared.CC;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.redis.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DlmsDevOperMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;

import redis.clients.jedis.BinaryJedis;

public class ClientDaoRedisImpl implements ClientDao, CC {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientDaoRedisImpl.class);

	@Override
	public void saveDeviceOperation(DlmsDevOperMsg deviceOperation) {
		jedis().set(key(RK_DEVICE_OPERATION, makeUniqueId()), deviceOperation.toByteArray());
	}

	@Override
	public synchronized String saveBundledDeviceOperation(RequestResponseMsg dlmsRegRespMsg) {
		final String correlid = dlmsRegRespMsg.getCorrelId();
		jedis().set(key(RK_BUNDLED_DEVOP, correlid), dlmsRegRespMsg.toByteArray());
		return correlid;
	}

	@Override
	public List<RequestResponseMsg> getAllBundledDeviceOperations() {
		List<RequestResponseMsg> r = new ArrayList<>();
		Set<byte[]> keys = jedis().keys(scanKey(RK_BUNDLED_DEVOP));
		for (byte[] key : keys) {
			try {
				RequestResponseMsg reqRespMsg = RequestResponseMsg.parseFrom(((byte[]) jedis().get(key)));
				r.add(reqRespMsg);
			} catch (Exception e) {
				LOGGER.error("error parsing record " + e);
			}
		}
		return r;
	}

	@Override
	public List<DeviceOperationTuple> getAllDeviceOperations() {
		List<DeviceOperationTuple> r = new ArrayList<>();
		Set<byte[]> keys = jedis().keys(scanKey(RK_DEVICE_OPERATION));
		for (byte[] key : keys) {
			try {
				DlmsDevOperMsg devopMsg = DlmsDevOperMsg.parseFrom(jedis().get(key));
				r.add(new DeviceOperationTuple(new PK(key), devopMsg));
			} catch (InvalidProtocolBufferException e) {
				LOGGER.error("error parsing record " + e);
			}
		}
		return r;
	}

	@Override
	public DlmsDevOperMsg getDeviceOperation(PK pk) {
		Object obj = jedis().get(pk.redisKey());
		if (obj instanceof Long) {
			System.out.println("## " + new String(pk.redisKey()));
		}
		byte[] bytes = jedis().get(pk.redisKey());
		if (bytes != null) {
			try {
				return DlmsDevOperMsg.parseFrom((byte[]) bytes);
			} catch (InvalidProtocolBufferException e) {
				LOGGER.error("error parsing record " + e);
				return null;
			}
		} else {
			return null;
		}
	}
	

	@Override
	public byte[] get(PK pk) {
		return jedis().get(pk.redisKey());
	}
	
	@Override
	public void delete(PK pk) {
		jedis().del(pk.redisKey());
	}

	private byte[] key(final String prefix, final String keyValue) {
		return RedisUtils.key(prefix, keyValue);
	}

	private byte[] scanKey(final String prefix) {
		return RedisUtils.scanKey(prefix);
	}

	private String makeUniqueId() {
		return UUID.randomUUID().toString();
	}

	private static BinaryJedis jedis() {
		return ClientDbsMgr.INSTANCE.dbsMgr().getRedisPool().jedis();
	}
}
