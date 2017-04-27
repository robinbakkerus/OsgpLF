package org.osgp.client.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.garret.perst.FieldIndex;
import org.osgp.client.dao.perst.ClientPerstRoot;
import org.osgp.client.dao.perst.PerstBundleMsg;
import org.osgp.client.dao.perst.PerstDevOperMsg;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.perst.PbMsgPerst;
import org.osgp.shared.dbs.perst.PerstUtils;
import org.osgp.util.dao.PK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DlmsDevOperMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;

public class ClientDaoPerstImpl implements ClientDao, CC {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientDaoPerstImpl.class);

	@Override
	public void saveDeviceOperation(DlmsDevOperMsg deviceOperation) {
		synchronized (ClientDaoPerstImpl.class) {
			save(devOperIndex(),
					new PerstDevOperMsg(key(RK_DEVICE_OPERATION, makeUniqueId()), deviceOperation.toByteArray()));
		}
	}

	@Override
	public synchronized String saveBundledDeviceOperation(RequestResponseMsg dlmsRegRespMsg) {
		synchronized (ClientDaoPerstImpl.class) {
			final String correlid = dlmsRegRespMsg.getCorrelId();
			save(bundleIndex(), new PerstBundleMsg(key(RK_BUNDLED_DEVOP, correlid), dlmsRegRespMsg.toByteArray()));
			return correlid;
		}
	}

	@Override
	public List<RequestResponseMsg> getAllBundledDeviceOperations() {
		synchronized (ClientDaoPerstImpl.class) {
			List<RequestResponseMsg> r = new ArrayList<>();
			for (String key : PerstUtils.scanAll(bundleIndex(), RK_BUNDLED_DEVOP)) {
				try {
					r.add(RequestResponseMsg.parseFrom(getBytes(bundleIndex(), key)));
				} catch (Exception e) {
					LOGGER.error("error parsing record " + e);
				}
			}
			return r;
		}
	}

	@Override
	public List<DeviceOperationTuple> getAllDeviceOperations() {
		synchronized (ClientDaoPerstImpl.class) {
			List<DeviceOperationTuple> r = new ArrayList<>();
			for (String key : PerstUtils.scanAll(devOperIndex(), RK_DEVICE_OPERATION)) {
				try {
					DlmsDevOperMsg devopMsg = DlmsDevOperMsg.parseFrom(getBytes(devOperIndex(), key));
					r.add(new DeviceOperationTuple(new PK(key), devopMsg));
				} catch (InvalidProtocolBufferException e) {
					LOGGER.error("error parsing record " + e);
				}
			}
			return r;
		}
	}

	@Override
	public DlmsDevOperMsg getDeviceOperation(PK pk) {
		synchronized (ClientDaoPerstImpl.class) {
			byte[] bytes = getBytes(devOperIndex(), pk.perstKey());
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
	}

	@Override
	public void delete(PK pk) {
		synchronized (ClientDaoPerstImpl.class) {
			if (pk.perstKey().startsWith(RK_BUNDLED_DEVOP)) {
				deleteBundle(pk);
			} else if (pk.perstKey().startsWith(RK_DEVICE_OPERATION)) {
				deleteDeviceOperation(pk);
			} else {
				throw new RuntimeException("CoreDaoPerstImpl.get() does not support " + pk.perstKey());
			}
		}
	}

	private void deleteDeviceOperation(PK pk) {
		byte[] bytes = getBytes(devOperIndex(), pk.perstKey());
		if (bytes != null) {
			PerstDevOperMsg obj = new PerstDevOperMsg(pk.perstKey(), bytes);
			devOperIndex().remove(obj);
			obj.deallocate();
		}
	}

	private void deleteBundle(PK pk) {
		byte[] bytes = getBytes(bundleIndex(), pk.perstKey());
		if (bytes != null) {
			PerstBundleMsg obj = new PerstBundleMsg(pk.perstKey(), bytes);
			bundleIndex().remove(obj);
			obj.deallocate();
		}
	}

	private String makeUniqueId() {
		return UUID.randomUUID().toString();
	}

	private String key(final String prefix, final String keyValue) {
		return prefix + keyValue;
	}

	@Override
	public byte[] get(PK pk) {
		synchronized (ClientDaoPerstImpl.class) {
			if (pk.perstKey().startsWith(RK_BUNDLED_DEVOP)) {
				return getBytes(bundleIndex(), pk.perstKey());
			} else if (pk.perstKey().startsWith(RK_DEVICE_OPERATION)) {
				return getBytes(devOperIndex(), pk.perstKey());
			} else {
				throw new RuntimeException("CoreDaoPerstImpl.get() does not support " + pk.perstKey());
			}
		}
	}

	private void save(final FieldIndex<PerstDevOperMsg> index, PerstDevOperMsg perstMsg) {
		try {
			if (index.get(perstMsg.strKey) != null) {
				index.remove(perstMsg);
			}
			index.put(perstMsg);
		} catch (Exception ex) {
			LOGGER.error("error while saving " + perstMsg + " in " + ex);
		}
	}

	private void save(final FieldIndex<PerstBundleMsg> index, PerstBundleMsg perstMsg) {
		try {
			if (index.get(perstMsg.strKey) != null) {
				index.remove(perstMsg);
			}
			index.put(perstMsg);
		} catch (Exception ex) {
			LOGGER.error("error while saving " + perstMsg + " in " + ex);
		}
	}

	private byte[] getBytes(final FieldIndex<?> index, final String key) {
		PbMsgPerst msg = (PbMsgPerst) index.get(key);
		return msg == null ? null : msg.body;
	}

	private FieldIndex<PerstDevOperMsg> devOperIndex() {
		return root().devOperIndex;
	}

	private FieldIndex<PerstBundleMsg> bundleIndex() {
		return root().bundleIndex;
	}

	private ClientPerstRoot root() {
		return ClientDbsMgr.INSTANCE.dbsMgr().getPerstRoot();
	}
}
