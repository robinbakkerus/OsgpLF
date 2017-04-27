package org.osgp.pa.dlms.application.dao;

import java.util.List;

import org.osgp.dlms.DC;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.shared.CC;
import org.osgp.shared.exceptionhandling.ComponentType;
import org.osgp.shared.exceptionhandling.FunctionalException;
import org.osgp.shared.exceptionhandling.FunctionalExceptionType;
import org.osgp.util.dao.PK;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.RecordExistsAction;
import com.aerospike.client.policy.WritePolicy;
import com.alliander.osgp.dlms.DlmsDeviceMsg;
import com.google.protobuf.InvalidProtocolBufferException;

public class DlmsDaoAerospikeImpl implements DlmsDao {

	private static final String NAMESPACE = DC.DBS_NAMESPACE_DLMS;

    private static final ComponentType COMPONENT_TYPE = ComponentType.PROTOCOL_DLMS;

	@Override
	public DlmsDeviceMsg findByDeviceId(String deviceId) throws FunctionalException {
		try {
			Key key = getKey(NAMESPACE, DC.TABLE_DLMS_DEVICE, deviceId);
			Record rec = client().get(null, key);
			if (rec != null) {
				return DlmsDeviceMsg.parseFrom((byte[]) rec.getValue(DC.PB_DATA));
			} else {
				throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, COMPONENT_TYPE,
						new ProtocolAdapterException("Unable to communicate with unknown device: " + deviceId));

			}
		} catch (InvalidProtocolBufferException e) {
			throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, COMPONENT_TYPE, e);
		}
	}

	@Override
	public DlmsDeviceMsg save(DlmsDeviceMsg dlmsDeviceMsg) {
		Key key = new Key(DC.DBS_NAMESPACE_DLMS, DC.TABLE_DLMS_DEVICE, dlmsDeviceMsg.getIdentification());
		WritePolicy wPolicy = new WritePolicy();
		wPolicy.recordExistsAction = RecordExistsAction.UPDATE;
		Bin bins = new Bin(CC.PB_DATA, dlmsDeviceMsg.toByteArray());
		client().put(wPolicy, key, bins);
		return dlmsDeviceMsg;
	}
	
	@Override
	public void saveList(List<DlmsDeviceMsg> dlmsDeviceList) {
		dlmsDeviceList.forEach(this::save);
	}

	@Override
	public Record get(PK pk) {
		return client().get(new Policy(), pk.aeroKey());
	}

	// ----------------

	@Override
	public void commit() {
	}
	
	// ----------------

	private Key getKey(String namespace, String setName, String deviceId) {
		return new Key(namespace, setName, deviceId);
	}
	
	private AerospikeClient client() {
		return DlmsDbsMgr.INSTANCE.dbsMgr().getAeroServer().getClient();
	}

}
