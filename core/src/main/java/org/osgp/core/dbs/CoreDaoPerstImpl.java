package org.osgp.core.dbs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.garret.perst.FieldIndex;
import org.osgp.core.dbs.perst.CorePerstRoot;
import org.osgp.core.dbs.perst.PerstDeviceMsg;
import org.osgp.core.dbs.perst.PerstScheduleMsg;
import org.osgp.core.dbs.perst.PerstUndeliveredMsg;
import org.osgp.shared.CC;
import org.osgp.shared.dbs.UndeliveredTuple;
import org.osgp.shared.dbs.perst.PbMsgPerst;
import org.osgp.util.dao.PK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.DeviceMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;

public class CoreDaoPerstImpl implements CoreDao, CC {

	private static final Logger LOGGER = LoggerFactory.getLogger(CoreDaoPerstImpl.class);

	public CoreDaoPerstImpl() {
		super();
	}

	@Override
	public DeviceMsg findDevice(RequestResponseMsg request) {
		synchronized (CoreDaoPerstImpl.class) {
			DeviceMsg device = null;
			String deviceId = request.getCommon().getDeviceId();
			byte[] bytes = getBytes(deviceIndex(), key(RK_CORE_DEVICE, deviceId));
			if (bytes != null) {
				try {
					device = DeviceMsg.parseFrom((byte[]) bytes);
				} catch (InvalidProtocolBufferException e) {
					LOGGER.error("error decoding device " + deviceId, e);
				}
			} else {
				LOGGER.error("could not find device " + deviceId);
			}
			return device;
		}
	}

	@Override
	public void saveReqRespToScheduleRepo(RequestResponseMsg request) {
		synchronized (CoreDaoPerstImpl.class) {
			String correlId = request.getCorrelId();
			save(scheduleIndex(), new PerstScheduleMsg(key(RK_CORE_SCHEDTASKS, correlId), request.toByteArray()));
		}
	}

	@Override
	public RequestResponseMsg findScheduledReqRespMsg(String correlid) {
		synchronized (CoreDaoPerstImpl.class) {
			byte[] bytes = getBytes(scheduleIndex(), key(RK_CORE_SCHEDTASKS, correlid));
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
	}

	@Override
	public List<ScheduledTaskTuple> getAllScheduledTasks() {
		synchronized (CoreDaoPerstImpl.class) {
			List<ScheduledTaskTuple> r = new ArrayList<>();

			for (String key : scanAll(scheduleIndex(), RK_CORE_SCHEDTASKS)) {
				try {
					byte bytes[] = getBytes(scheduleIndex(), key);
					if (bytes != null) {
						RequestResponseMsg reqRespMsg = RequestResponseMsg.parseFrom(bytes);
						r.add(new ScheduledTaskTuple(new PK(key), reqRespMsg));
					}
				} catch (InvalidProtocolBufferException e) {
					LOGGER.error("error parsing record " + e);
				}
			}
			return r;
		}
	}

	@Override
	public void saveCoreDevice(DeviceMsg deviceMsg) {
		save(deviceIndex(), new PerstDeviceMsg(key(RK_CORE_DEVICE, deviceMsg.getDeviceId()), deviceMsg.toByteArray()));
	}

	@Override
	public void saveCoreDevices(List<DeviceMsg> deviceMsgList) {
		deviceMsgList.forEach(this::saveCoreDevice);
	}

	@Override
	public DeviceMsg getCoreDevice(String deviceId) {
		synchronized (CoreDaoPerstImpl.class) {
			try {
				byte[] bytes = getBytes(deviceIndex(), key(RK_CORE_DEVICE, deviceId));
				return bytes == null ? null : DeviceMsg.parseFrom((byte[]) bytes);
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	@Override
	public void saveUndeliveredRequest(RequestResponseMsg reqRespMsg) {
		synchronized (CoreDaoPerstImpl.class) {
			save(undeliveredIndex(), new PerstUndeliveredMsg(key(RK_CORE2PA_UNDELIVERED, reqRespMsg.getCorrelId()),
					reqRespMsg.toByteArray()));
		}
	}

	@Override
	public List<UndeliveredTuple> getAllUndeliveredRequests() {
		synchronized (CoreDaoPerstImpl.class) {
			List<UndeliveredTuple> r = new ArrayList<>();

			for (String key : scanAll(undeliveredIndex(), RK_CORE2PA_UNDELIVERED)) {
				try {
					RequestResponseMsg reqRespMsg = RequestResponseMsg
							.parseFrom(((byte[]) getBytes(undeliveredIndex(), key)));
					r.add(new UndeliveredTuple(new PK(key), reqRespMsg));
				} catch (InvalidProtocolBufferException e) {
					LOGGER.error("error parsing record " + e);
				}
			}
			return r;
		}
	}

	// ------------------

	@Override
	public List<PK> getAllScheduledTaskPks() {
		synchronized (CoreDaoPerstImpl.class) {
			return getAllPks(scheduleIndex(), RK_CORE_SCHEDTASKS);
		}
	}

	public static List<PK> getAllPks(final FieldIndex<?> index, final String prefix) {
		synchronized (CoreDaoPerstImpl.class) {
			List<PK> r = new ArrayList<>();
			for (String key : scanAll(index, prefix)) {
				try {
					r.add(new PK(key));
				} catch (Exception e) {
					LOGGER.error("error parsing record " + e);
				}
			}
			return r;
		}
	}

	// --------------
//	@Override
//	public byte[] get(PK pk) {
//		synchronized (CoreDaoPerstImpl.class) {
//			if (pk.perstKey().startsWith(RK_CORE_DEVICE)) {
//				return getBytes(deviceIndex(), pk.perstKey());
//			} else if (pk.perstKey().startsWith(RK_CORE_SCHEDTASKS)) {
//				return getBytes(scheduleIndex(), pk.perstKey());
//			} else if (pk.perstKey().startsWith(RK_CORE2PA_UNDELIVERED)) {
//				return getBytes(undeliveredIndex(), pk.perstKey());
//			} else {
//				throw new RuntimeException("CoreDaoPerstImpl.get() does not support " + pk.perstKey());
//			}
//		}
//	}

	@Override
	public void delete(PK pk) {
		synchronized (CoreDaoPerstImpl.class) {
			if (pk.perstKey().startsWith(RK_CORE_DEVICE)) {
				byte[] bytes = getBytes(deviceIndex(), pk.perstKey());
				if (bytes != null) {
					PerstDeviceMsg obj = new PerstDeviceMsg(pk.perstKey(), bytes);
					deviceIndex().remove(obj);
					obj.deallocate();
				}
			} else if (pk.perstKey().startsWith(RK_CORE_SCHEDTASKS)) {
				byte[] bytes = getBytes(scheduleIndex(), pk.perstKey());
				if (bytes != null) {
					PerstScheduleMsg obj = new PerstScheduleMsg(pk.perstKey(), bytes);
					scheduleIndex().remove(obj);
					obj.deallocate();
				}
			} else if (pk.perstKey().startsWith(RK_CORE2PA_UNDELIVERED)) {
				byte[] bytes = getBytes(undeliveredIndex(), pk.perstKey());
				if (bytes != null) {
					PerstUndeliveredMsg obj = new PerstUndeliveredMsg(pk.perstKey(), bytes);
					undeliveredIndex().remove(obj);
					obj.deallocate();
				}
			} else {
				throw new RuntimeException("CoreDaoPerstImpl.get() does not support " + pk.perstKey());
			}
		}
	}

	@Override
	public void commit() {
		root().getStorage().commit();
	}

	// ---------------

	public static List<String> scanAll(final FieldIndex<?> index, final String prefix) {
		List<String> r = new ArrayList<>();
		Iterator<?> iter = index.iterator();
		while (iter.hasNext()) {
			PbMsgPerst perstMsg = (PbMsgPerst) iter.next();
			r.add(perstMsg.strKey);
		}
		return r;
	}

	// ----------------
	private String key(final String prefix, final String keyValue) {
		return prefix + keyValue;
	}

	private void save(final FieldIndex<PerstDeviceMsg> index, PerstDeviceMsg perstMsg) {
		try {
			if (index.get(perstMsg.strKey) != null) {
				index.remove(perstMsg);
			}
			index.put(perstMsg);
		} catch (Exception ex) {
			LOGGER.error("error while saving " + perstMsg + " in " + ex);
		}
	}

	private void save(final FieldIndex<PerstScheduleMsg> index, PerstScheduleMsg perstMsg) {
		try {
			if (index.get(perstMsg.strKey) != null) {
				index.remove(perstMsg);
			}
			index.put(perstMsg);
		} catch (Exception ex) {
			LOGGER.error("error while saving " + perstMsg + " in " + ex);
		}
	}

	private void save(final FieldIndex<PerstUndeliveredMsg> index, PerstUndeliveredMsg perstMsg) {
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

	private FieldIndex<PerstDeviceMsg> deviceIndex() {
		return root().deviceIndex;
	}

	private FieldIndex<PerstScheduleMsg> scheduleIndex() {
		return root().scheduleIndex;
	}

	private FieldIndex<PerstUndeliveredMsg> undeliveredIndex() {
		return root().undeliverdIndex;
	}

	private CorePerstRoot root() {
		return CoreDbsMgr.INSTANCE.dbsMgr().getPerstRoot();
	}
}
