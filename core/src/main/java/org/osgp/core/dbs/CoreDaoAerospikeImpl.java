package org.osgp.core.dbs;

import java.util.ArrayList;
import java.util.List;

import org.osgp.shared.CC;
import org.osgp.shared.dbs.UndeliveredTuple;
import org.osgp.util.dao.PK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.ScanCallback;
import com.aerospike.client.policy.Priority;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.alliander.osgp.shared.DeviceMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;

import static org.osgp.core.dbs.CoreCassandraClient.*;

public class CoreDaoAerospikeImpl implements CoreDao, CC {

	private static final Logger LOGGER = LoggerFactory.getLogger(CoreDaoAerospikeImpl.class.getName());

	@Override
	public DeviceMsg findDevice(RequestResponseMsg request) {
		DeviceMsg device = null;
		String deviceId = request.getCommon().getDeviceId();
		final Key key = makeDeviceKey(deviceId);
		Record record = dbsClient().get(null, key);
		if (record != null) {
			try {
				device = decodeRecord(record);
			} catch (InvalidProtocolBufferException e) {
				LOGGER.error("error decoding device " + deviceId, e);
			}
		} else {
			LOGGER.error("could not find device " + deviceId);
		}
		return device;
	}

	private DeviceMsg decodeRecord(Record record) throws InvalidProtocolBufferException {
		return DeviceMsg.parseFrom((byte[]) record.getValue(CC.PB_DATA));
	}

	// -------------

	@Override
	public void saveReqRespToScheduleRepo(RequestResponseMsg msg) {
		final Key key = makeScheduleKey(makeScheduleKeyId(msg));
		WritePolicy wPolicy = new WritePolicy();
		wPolicy.recordExistsAction = RecordExistsAction.UPDATE;
		dbsClient().put(wPolicy, key, new Bin(PB_DATA, msg.toByteArray()));
	}

	@Override
	public RequestResponseMsg findScheduledReqRespMsg(String correlid) {
		final Key key = makeScheduleKey(makeScheduleKeyId(correlid));
		Record rec = dbsClient().get(null, key);
		if (rec != null) {
			try {
				RequestResponseMsg r = RequestResponseMsg.parseFrom((byte[]) rec.getValue(CC.PB_DATA));
				return r;
			} catch (InvalidProtocolBufferException e) {
				LOGGER.error("Error getting scheduled request " + correlid, e);
				return null;
			}
		} else {
			return null;
		}
	}
	
	@Override
	public List<ScheduledTaskTuple> getAllScheduledTasks() {
		List<ScheduledTaskTuple> result = new ArrayList<>();
		List<Key> delKeys = new ArrayList<>();
		final long now = System.nanoTime();

		try {
			ScanPolicy policy = scanPolicy();
			dbsClient().scanAll(policy, DBS_NAMESPACE_CORE, TABLE_CORE_SCHEDULE, new ScanCallback() {
				@Override
				public void scanCallback(Key key, Record rec) throws AerospikeException {
					try {
						RequestResponseMsg msg = RequestResponseMsg.parseFrom((byte[]) rec.getValue(CC.PB_DATA));
						final long schedtime = msg.getCommon().getScheduleTime();
						if (now > schedtime) {
							result.add(new ScheduledTaskTuple(new PK(key), msg));
						}
					} catch (InvalidProtocolBufferException e) {
						LOGGER.error("error parsing record ", e);
						delKeys.add(key);
					}
				}
			}, CC.PB_DATA);
		} catch (AerospikeException e) {
			LOGGER.error("EXCEPTION - Message: " + e.getMessage(), e);
		}
		
		delKeys.forEach(k -> this.delete(new PK(k)));

		return result;
	}

	@Override
	public void delete(PK pk) {
		dbsClient().delete(null, pk.aeroKey());
	}

	private Key makeDeviceKey(final String schedKey) {
		return new Key(DBS_NAMESPACE_CORE, TABLE_CORE_DEVICE, schedKey);
	}

	private Key makeScheduleKey(final String schedKey) {
		return new Key(DBS_NAMESPACE_CORE, TABLE_CORE_SCHEDULE, schedKey);
	}

	private String makeScheduleKeyId(RequestResponseMsg msg) {
		return makeScheduleKeyId(msg.getCorrelId());
	}

	private String makeScheduleKeyId(String correlid) {
		final String schedKey = PREFIX_SCHEDULE + correlid;
		return schedKey;
	}

	private ScanPolicy scanPolicy() {
		ScanPolicy policy = new ScanPolicy();
		policy.concurrentNodes = true;
		policy.priority = Priority.LOW;
		policy.includeBinData = true;
		return policy;
	}

	// --- insertCoreDevice

	@Override
	public void saveCoreDevice(final DeviceMsg deviceMsg) {
		Key key = new Key(CC.DBS_NAMESPACE_CORE, TABLE_CORE_DEVICE, deviceMsg.getDeviceId());
		WritePolicy wPolicy = new WritePolicy();
		wPolicy.recordExistsAction = RecordExistsAction.UPDATE;
		Bin bins = new Bin(CC.PB_DATA, deviceMsg.toByteArray());
		dbsClient().put(wPolicy, key, bins);
	}
	
	
	@Override
	public void saveCoreDevices(List<DeviceMsg> deviceMsgList) {
		deviceMsgList.forEach(this::saveCoreDevice);
	}

	@Override
	public DeviceMsg getCoreDevice(final String deviceId) {
		try {
			Key key = new Key(CC.DBS_NAMESPACE_CORE, TABLE_CORE_DEVICE, deviceId);
			Record rec = dbsClient().get(null, key);
			return rec == null ? null : DeviceMsg.parseFrom((byte[]) rec.getValue(CC.PB_DATA));
		} catch (InvalidProtocolBufferException e) {
			LOGGER.error("error parsing record ", e);
			return null;
		}
	}


	//----- saveUndeliveredRequest
	
	@Override
	public void saveUndeliveredRequest(RequestResponseMsg reqRespMsg) {
		Key key = new Key(DBS_NAMESPACE_CORE, TABLE_UNDELIVERED_DLMS, getCorrelId(reqRespMsg));
		Bin bin = new Bin(BIN_UNDELIVERED_DLMS, reqRespMsg.toByteArray());
		WritePolicy wPolicy = new WritePolicy();
		wPolicy.recordExistsAction = RecordExistsAction.CREATE_ONLY;
		dbsClient().put(wPolicy, key, bin);		
	}

	//------------ getAllUndeliveredRequests
	
	@Override
	public List<UndeliveredTuple> getAllUndeliveredRequests() {
		List<UndeliveredTuple> r = new ArrayList<>();

		try {
			dbsClient().scanAll(scanPolicy(), CC.DBS_NAMESPACE_CORE, TABLE_UNDELIVERED_DLMS,
					new ScanCallback() {

						@Override
						public void scanCallback(Key key, Record rec)  {
							try {
								RequestResponseMsg devop = RequestResponseMsg
										.parseFrom((byte[]) rec.getValue(BIN_UNDELIVERED_DLMS));
								r.add(new UndeliveredTuple(new PK(key), devop));
							} catch (InvalidProtocolBufferException e) {
								LOGGER.error("error parsing record " + e);
							}
						}
					}, BIN_UNDELIVERED_DLMS);
		} catch (AerospikeException e) {
			LOGGER.error("EXCEPTION - Message: " + e.getMessage(), e);
		}

		return r;
	}

	//--------------
	
	@Override
	public List<PK> getAllScheduledTaskPks() {
		List<PK> r = new ArrayList<>();

		try {
			ScanPolicy policy = scanPolicy();

			dbsClient().scanAll(policy, CC.DBS_NAMESPACE_CORE, TABLE_CORE_SCHEDULE,
					new ScanCallback() {

						@Override
						public void scanCallback(Key key, Record rec) {
							try {
								r.add(new PK(key));
							} catch (Exception e) {
								LOGGER.error("error parsing record " + e);
							}
						}
					}, CC.PB_DATA);
		} catch (AerospikeException e) {
			LOGGER.error("EXCEPTION - Message: " + e.getMessage(), e);
		}

		return r;
	}
	

	//---------
	

	@Override
	public void commit() {
	}
	
	// ----------------

	private String getCorrelId(RequestResponseMsg request) {
		return request.getCorrelId();
	}

	private AerospikeClient dbsClient() {
		return CoreDbsMgr.INSTANCE.dbsMgr().getAeroServer().getClient();
	}

}
