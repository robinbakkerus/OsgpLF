package org.osgp.core.dbs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.osgp.shared.CC;
import org.osgp.shared.dbs.UndeliveredTuple;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.redis.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.DeviceMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;

import redis.clients.jedis.BinaryJedis;

public class CoreDaoRedisImpl implements CoreDao, CC {

	private static final Logger LOGGER = LoggerFactory.getLogger(CoreDaoRedisImpl.class);

	@Override
	public DeviceMsg findDevice(RequestResponseMsg request) {
		DeviceMsg device = null;
		String deviceId = request.getCommon().getDeviceId();
		byte[] bytes = jedis().get(key(RK_CORE_DEVICE, deviceId));
		if (bytes != null) {
			try {
				device = DeviceMsg.parseFrom((byte[]) bytes);
			} catch (InvalidProtocolBufferException e) {
				LOGGER.error("error decoding device " + deviceId);
			}
		} else {
			LOGGER.error("could not find device " + deviceId);
		}
		return device;
	}

	@Override
	public void saveReqRespToScheduleRepo(RequestResponseMsg request) {
		String correlId = request.getCorrelId();
		jedis().set(key(RK_CORE_SCHEDTASKS, correlId), request.toByteArray());
	}

	@Override
	public RequestResponseMsg findScheduledReqRespMsg(String correlid) {
		byte[] bytes = jedis().get(key(RK_CORE_SCHEDTASKS, correlid));
		if (bytes != null) {
			try {
				RequestResponseMsg r = RequestResponseMsg.parseFrom((byte[]) bytes);
				return r;
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			// todo error handling etc
			return null;
		}
	}

	@Override
	public List<ScheduledTaskTuple> getAllScheduledTasks() {
		List<ScheduledTaskTuple> r = new ArrayList<>();
		Set<byte[]> keys = jedis().keys(scanKey(RK_CORE_SCHEDTASKS));
		for (byte[] key : keys) {
			try {
				RequestResponseMsg reqRespMsg = RequestResponseMsg.parseFrom(((byte[]) jedis().get(key)));
				r.add(new ScheduledTaskTuple(new PK(key), reqRespMsg));
			} catch (Exception e) {
				LOGGER.error("error parsing record " + e);
			}
		}
		return r;
	}
	
	@Override
	public List<PK> getAllScheduledTaskPks() {
		List<PK> r = new ArrayList<>();
		Set<byte[]> keys = jedis().keys(scanKey(RK_CORE_SCHEDTASKS));
		for (byte[] key : keys) {
			r.add(new PK(key));
		}
		return r;
	}


	@Override
	public void saveCoreDevice(DeviceMsg deviceMsg) {
		jedis().set(key(RK_CORE_DEVICE, deviceMsg.getDeviceId()), deviceMsg.toByteArray());
	}
	
	@Override
	public void saveCoreDevices(List<DeviceMsg> deviceMsgList) {
		deviceMsgList.forEach(this::saveCoreDevice);
	}


	@Override
	public DeviceMsg getCoreDevice(String deviceId) {
		try {
			byte[] bytes = jedis().get(key(RK_CORE_DEVICE, deviceId));
			return bytes == null ? null : DeviceMsg.parseFrom((byte[]) bytes);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void saveUndeliveredRequest(RequestResponseMsg reqRespMsg) {
		jedis().set(key(RK_CORE2PA_UNDELIVERED, reqRespMsg.getCorrelId()), reqRespMsg.toByteArray());
	}

	@Override
	public List<UndeliveredTuple> getAllUndeliveredRequests() {
		List<UndeliveredTuple> r = new ArrayList<>();
		Set<byte[]> keys = jedis().keys(scanKey(RK_CORE2PA_UNDELIVERED));
		for (byte[] key : keys) {
			try {
				RequestResponseMsg reqRespMsg = RequestResponseMsg.parseFrom(((byte[]) jedis().get(key)));
				r.add(new UndeliveredTuple(new PK(key), reqRespMsg));
			} catch (Exception e) {
				LOGGER.error("error parsing record " + e);
			}
		}
		return r;
	}
	
	
//	@Override
//	public byte[] get(PK pk) {
//		return jedis().get(pk.redisKey());
//	}
	
	@Override
	public void delete(PK pk) {
		jedis().del(pk.redisKey());
	}
	
	@Override
	public void commit() {
	}

	//--------------------

	private byte[] key(final String prefix, final String keyValue) {
		return RedisUtils.key(prefix, keyValue);
	}

	private byte[] scanKey(final String prefix) {
		return RedisUtils.scanKey(prefix);
	}

	private BinaryJedis jedis() {
		return CoreDbsMgr.INSTANCE.dbsMgr().getRedisPool().jedis();
	}
}
