package org.osgp.pa.dlms.application.dao;

import java.util.List;

import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.shared.CC;
import org.osgp.shared.exceptionhandling.ComponentType;
import org.osgp.shared.exceptionhandling.FunctionalException;
import org.osgp.shared.exceptionhandling.FunctionalExceptionType;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.redis.RedisUtils;

import com.alliander.osgp.dlms.DlmsDeviceMsg;
import com.google.protobuf.InvalidProtocolBufferException;

import redis.clients.jedis.BinaryJedis;

public class DlmsDaoRedisImpl implements DlmsDao, CC {

	@Override
	public DlmsDeviceMsg findByDeviceId(String deviceId) throws FunctionalException {
		try {
			byte[] bytes = jedis().get(key(RK_DLMS_DEVICE, deviceId));
			if (bytes != null) {
				return DlmsDeviceMsg.parseFrom((byte[]) bytes);
			} else {
				throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, ComponentType.PROTOCOL_DLMS,
						new ProtocolAdapterException("Unable to communicate with unknown device: " + deviceId));

			}
		} catch (InvalidProtocolBufferException e) {
			throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, ComponentType.PROTOCOL_DLMS, e);
		}
	}

	@Override
	public DlmsDeviceMsg save(DlmsDeviceMsg dlmsDeviceMsg) {
		jedis().set(key(RK_DLMS_DEVICE, dlmsDeviceMsg.getIdentification()), dlmsDeviceMsg.toByteArray());
		return dlmsDeviceMsg;
	}

	@Override
	public void saveList(List<DlmsDeviceMsg> dlmsDeviceList) {
		dlmsDeviceList.forEach(this::save);
	}

	@Override
	public byte[] get(PK pk) {
		return jedis().get(pk.redisKey());
	}

	// ----------------

	@Override
	public void commit() {
	}
	
	// ----------------
	
	private byte[] key(final String prefix, final String keyValue) {
		return RedisUtils.key(prefix, keyValue);
	}

	private BinaryJedis jedis() {
		return DlmsDbsMgr.INSTANCE.dbsMgr().getRedisPool().jedis();
	}
}
