package org.osgp.client.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.osgp.dlms.DC;
import org.osgp.shared.CC;
import org.osgp.util.dao.PK;
import org.slf4j.LoggerFactory;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.ScanCallback;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.Priority;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.alliander.osgp.dlms.DlmsDevOperMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;
import static org.osgp.core.dbs.CoreCassandraClient.*;
public class ClientDaoImplAerospike implements ClientDao, CC {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ClientDaoImplAerospike.class);

	@Override
	public void saveDeviceOperation(DlmsDevOperMsg deviceOperation) {
		WritePolicy wPolicy = new WritePolicy();
		wPolicy.recordExistsAction = RecordExistsAction.UPDATE;

		Key key = new Key(DC.DBS_NAMESPACE_SMINT, DC.DBS_BIN_DEVOPER, makeUniqueId());
		Bin bins = new Bin(CC.PB_DATA, deviceOperation.toByteArray());
		dbsClient().put(wPolicy, key, bins);
	}


	// --- RequestResponseMsg

	public void saveRequestResponseMsg(final RequestResponseMsg reqRespMsg) {
		final String correlId = reqRespMsg.getCorrelId();
		Key key = new Key(CC.DBS_NAMESPACE_CORE, TABLE_CORE_SCHEDULE, correlId);
		WritePolicy wPolicy = new WritePolicy();
		wPolicy.recordExistsAction = RecordExistsAction.UPDATE;
		dbsClient().put(wPolicy, key, new Bin(PB_DATA, reqRespMsg.toByteArray()));
	}

	public RequestResponseMsg getRequestResponseMsg(final String correlId) {
		Key key = new Key(DC.DBS_NAMESPACE_PLATFORM, DC.TABLE_REQRESP_MSG, correlId);
		Record rec = dbsClient().get(null, key);
		if (rec != null) {
			try {
				return RequestResponseMsg.parseFrom((byte[]) rec.getValue(CC.PB_DATA));
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	// -----

	public List<DeviceOperationTuple> getAllDeviceOperations() {
		List<DeviceOperationTuple> r = new ArrayList<>();

		try {
			dbsClient().scanAll(scanPolicy(), DC.DBS_NAMESPACE_SMINT, DC.DBS_BIN_DEVOPER,
					new ScanCallback() {

						@Override
						public void scanCallback(Key key, Record rec) throws AerospikeException {
							try {
								DlmsDevOperMsg devop = DlmsDevOperMsg
										.parseFrom((byte[]) rec.getValue(CC.PB_DATA));
								r.add(new DeviceOperationTuple(new PK(key), devop));
							} catch (InvalidProtocolBufferException e) {
								LOGGER.error("error parsing record " + e);
							}
						}
					}, CC.PB_DATA);
		} catch (AerospikeException e) {
			LOGGER.error("EXCEPTION - Message: " + e.getMessage());
		}

		return r;
	}

	// --- getDeviceOperation

	@Override
	public DlmsDevOperMsg getDeviceOperation(PK pk) {
		Record rec = dbsClient().get(null, pk.aeroKey());
		if (rec != null) {
			try {
				return DlmsDevOperMsg.parseFrom((byte[]) rec.getValue(CC.PB_DATA));
			} catch (InvalidProtocolBufferException e) {
				LOGGER.error("error parsing record " + e);
				return null;
			}
		} else {
			return null;
		}
	}

	// ---- saveDlmsReqRespMsg

	@Override
	public String saveBundledDeviceOperation(RequestResponseMsg dlmsRegRespMsg) {
		final String correlid = dlmsRegRespMsg.getCorrelId();
		Key newkey = new Key(DC.DBS_NAMESPACE_SMINT, DC.DBS_BIN_BUNDLE_SEND, correlid);
		Bin bin = new Bin(CC.PB_DATA, dlmsRegRespMsg.toByteArray());
		dbsClient().put(null, newkey, bin);
		return correlid;
	}

	//--------------
	
	@Override
	public List<RequestResponseMsg> getAllBundledDeviceOperations() {
		List<RequestResponseMsg> r = new ArrayList<>();

		try {
			ScanPolicy policy = scanPolicy();

			dbsClient().scanAll(policy, DC.DBS_NAMESPACE_SMINT, DC.DBS_BIN_BUNDLE_SEND,
					new ScanCallback() {
						@Override
						public void scanCallback(Key key, Record rec) throws AerospikeException {
							try {
								RequestResponseMsg reqRespMsg = 
										RequestResponseMsg.parseFrom(((byte[]) rec.getValue(CC.PB_DATA)));
								r.add(reqRespMsg);
							} catch (Exception e) {
								LOGGER.error("error parsing record " + e);
							}
						}
					}, CC.PB_DATA);
		} catch (AerospikeException e) {
			LOGGER.error("EXCEPTION - Message: " + e.getMessage());
		}

		return r;		
	}
	
	
	// --- delete


	@Override
	public void delete(PK pk) {
		dbsClient().delete(null, pk.aeroKey());
	}

	//---------------------
	
	@Override
	public Record get(PK pk) {
		return dbsClient().get(new Policy(), pk.aeroKey());
	}

	// ======================================

	private String makeUniqueId() {
		return UUID.randomUUID().toString();
	}

	private ScanPolicy scanPolicy() {
		ScanPolicy policy = new ScanPolicy();
		policy.concurrentNodes = true;
		policy.priority = Priority.LOW;
		policy.includeBinData = true;
		return policy;
	}

	private AerospikeClient dbsClient() {
		return ClientDbsMgr.INSTANCE.dbsMgr().getAeroServer().getClient();
	}

}
