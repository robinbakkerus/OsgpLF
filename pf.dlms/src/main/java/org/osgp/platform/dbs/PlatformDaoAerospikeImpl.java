package org.osgp.platform.dbs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgp.dlms.DC;
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
import com.alliander.osgp.shared.CorrelIdMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;

public class PlatformDaoAerospikeImpl implements PlatformDao, DC {

	private static final Logger LOGGER = LoggerFactory.getLogger(PlatformDaoAerospikeImpl.class.getName());

	private static final String NAMESPACE = DC.DBS_NAMESPACE_PLATFORM;
	
//	private static final String FIELDS[] = new String[] { CREATED_AT, MODIFIED_AT, REQ_RESP_MSG };

	private static long startedat = new Date().getTime();

	@Override
	public void saveRequestResponse(RequestResponseMsg reqRespMsg) {
		Key key = makeKey(reqRespMsg);
		Bin bin1 = new Bin(CORRELID, getCorrelId(reqRespMsg));
		Bin bin2 = new Bin(CREATED_AT, now());
		Bin bin3 = new Bin(MODIFIED_AT, "");
		Bin bin4 = new Bin(REQ_RESP_MSG, reqRespMsg.toByteArray());

		WritePolicy wPolicy = new WritePolicy();
		wPolicy.recordExistsAction = RecordExistsAction.UPDATE;
		dbsClient().put(wPolicy, key, bin1, bin2, bin3, bin4);
	}


	@Override
	public void saveRequestResponses(List<RequestResponseMsg> reqRespMsgList) {
		reqRespMsgList.forEach(this::saveRequestResponse);
	}


	@Override
	public RequestResponseMsg getResponse(final CorrelIdMsg correlIdMsg) throws InvalidProtocolBufferException {
		return getResponse(correlIdMsg.getCorrelid());
	}

	@Override
	public RequestResponseMsg getResponse(final String correlId) throws InvalidProtocolBufferException {
		Key key = makeKey(correlId);
		Record record = dbsClient().get(null, key);
		if (record != null) {
			return RequestResponseMsg.parseFrom((byte[]) record.getValue(REQ_RESP_MSG));
		} else {
			return null;
		}
	}

	//--------
	
	
	@Override
	public void saveUndeliveredRequest(RequestResponseMsg reqRespMsg) {
		Key key = new Key(NAMESPACE, TABLE_UNDELIVERED_CORE, getCorrelId(reqRespMsg));
		Bin bin = new Bin(BIN_UNDELIVERED_CORE, reqRespMsg.toByteArray());
		WritePolicy wPolicy = new WritePolicy();
		wPolicy.recordExistsAction = RecordExistsAction.CREATE_ONLY;
		dbsClient().put(wPolicy, key, bin);		
	}

	//------------ getAllUndeliveredRequests
	
	@Override
	public List<UndeliveredTuple> getAllUndeliveredRequests() {
		List<UndeliveredTuple> r = new ArrayList<>();

		try {
			dbsClient().scanAll(scanPolicy(), NAMESPACE, TABLE_UNDELIVERED_CORE,
					new ScanCallback() {

						@Override
						public void scanCallback(Key key, Record rec) throws AerospikeException {
							try {
								RequestResponseMsg devop = RequestResponseMsg
										.parseFrom((byte[]) rec.getValue(BIN_UNDELIVERED_CORE));
								r.add(new UndeliveredTuple(new PK(key), devop));
							} catch (InvalidProtocolBufferException e) {
								LOGGER.error("error parsing record " + e);
							}
						}
					}, BIN_UNDELIVERED_CORE);
		} catch (AerospikeException e) {
			LOGGER.error("EXCEPTION - Message: " + e.getMessage());
		}

		return r;
	}

	@Override
	public void delete(PlatformTable table, PK pk) {
		dbsClient().delete(null, pk.aeroKey());
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

	//---------- 
	@Override
	public List<PK> getAllRequestResponseMsgPKs() {
		List<PK> r = new ArrayList<>();

		try {
			ScanPolicy policy = scanPolicy();

			dbsClient().scanAll(policy, DC.DBS_NAMESPACE_PLATFORM, DC.TABLE_REQRESP_MSG,
					new ScanCallback() {
						@Override
						public void scanCallback(Key key, Record rec) throws AerospikeException {
							try {
								r.add(new PK(key));
							} catch (Exception e) {
								LOGGER.error("error parsing record " + e);
							}
						}
					}, DC.REQ_RESP_MSG);
		} catch (AerospikeException e) {
			LOGGER.error("EXCEPTION - Message: " + e.getMessage());
		}

		return r;				
	}
	
	

	//---- helpers 


	@Override
	public List<RequestResponseMsg> getAllRequestResponseMsgs() {
		// TODO Auto-generated method stub
		return null;
	}

	private Key makeKey(String correlid) {
		return new Key(NAMESPACE, TABLE_REQRESP_MSG, correlid);
	}

	private Key makeKey(RequestResponseMsg request) {
		return new Key(NAMESPACE, TABLE_REQRESP_MSG, getCorrelId(request));
	}


	private String getCorrelId(RequestResponseMsg request) {
		return request.getCorrelId();
	}


	private long now() {
		return new Date().getTime();
	}

	private ScanPolicy scanPolicy() {
		ScanPolicy policy = new ScanPolicy();
		policy.concurrentNodes = true;
		policy.priority = Priority.LOW;
		policy.includeBinData = true;
		return policy;
	}
	
	private AerospikeClient dbsClient() {
		return PlatformDbsMgr.INSTANCE.dbsMgr().getAeroServer().getClient();
	}

}
