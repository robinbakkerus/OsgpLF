package org.osgp.pa.dlms.application.dao;

import java.util.List;

import org.garret.perst.FieldIndex;
import org.osgp.pa.dlms.application.dao.perst.DlmsPerstRoot;
import org.osgp.pa.dlms.application.dao.perst.PerstDlmsDeviceMsg;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.perst.PbMsgPerst;
import org.osgp.shared.exceptionhandling.ComponentType;
import org.osgp.shared.exceptionhandling.FunctionalException;
import org.osgp.shared.exceptionhandling.FunctionalExceptionType;
import org.osgp.util.dao.PK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DlmsDeviceMsg;
import com.google.protobuf.InvalidProtocolBufferException;

public class DlmsDaoPerstImpl implements DlmsDao, CC {

	private static final Logger LOGGER = LoggerFactory.getLogger(DlmsDaoPerstImpl.class);

	public DlmsDaoPerstImpl() {
		super();
	}

	@Override
	public synchronized DlmsDeviceMsg findByDeviceId(String deviceId) throws FunctionalException {
		try {
			byte[] bytes = getBytes(key(RK_DLMS_DEVICE, deviceId));
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
		save(deviceIndex(), new PerstDlmsDeviceMsg(key(RK_DLMS_DEVICE, dlmsDeviceMsg.getIdentification()), dlmsDeviceMsg.toByteArray()));
		return dlmsDeviceMsg;
	}

	@Override
	public void saveList(List<DlmsDeviceMsg> dlmsDeviceList) {
		dlmsDeviceList.forEach(this::save);
	}

	// ----------------

	@Override
	public void commit() {
		root().getStorage().commit();
	}
	
	// ----------------

	@Override
	public byte[] get(PK pk) {
		return getBytes(deviceIndex(), pk.perstKey());
	}

	private byte[] getBytes(final FieldIndex<?> index, final String key) {
		PbMsgPerst msg = (PbMsgPerst) index.get(key);
		return msg == null ? null : msg.body;
	}
	
	private void save(final FieldIndex<PerstDlmsDeviceMsg> index, PerstDlmsDeviceMsg perstMsg) {
		try {
			if (index.get(perstMsg.strKey) != null) {
				index.remove(perstMsg);
			}
			index.put(perstMsg);
		} catch (Exception ex) {
			LOGGER.error("error while saving " + perstMsg + " in " + ex);
		}
	}
	
	private String key(final String prefix, final String keyValue) {
		return prefix + keyValue;
	}
	
	private byte[] getBytes(final String key) {
		PbMsgPerst msg = deviceIndex().get(key);
		return msg==null ? null : msg.body;
	}
	
	private FieldIndex<PerstDlmsDeviceMsg> deviceIndex() {
		return root().dlmsDeviceIndex;
	}

	private DlmsPerstRoot root() {
		return DlmsDbsMgr.INSTANCE.dbsMgr().getPerstRoot();
	}
	
}