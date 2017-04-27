package org.osgp.core.dbs.perst;

import org.garret.perst.FieldIndex;
import org.garret.perst.Persistent;
import org.garret.perst.Storage;
import org.osgp.shared.CC;

public class CorePerstRoot extends Persistent implements CC {

	public FieldIndex<PerstDeviceMsg> deviceIndex;
	public FieldIndex<PerstScheduleMsg> scheduleIndex;
	public FieldIndex<PerstUndeliveredMsg> undeliverdIndex;

	public CorePerstRoot() {
		super();
	}

	public CorePerstRoot(Storage storage) {
		super(storage);
		deviceIndex = storage.<PerstDeviceMsg> createFieldIndex(PerstDeviceMsg.class, "strKey", true);
		scheduleIndex = storage.<PerstScheduleMsg> createFieldIndex(PerstScheduleMsg.class, "strKey", true);
		undeliverdIndex = storage.<PerstUndeliveredMsg> createFieldIndex(PerstUndeliveredMsg.class, "strKey", true);
	}
	
	public FieldIndex<?> getFieldIndex(final String prefix) {
		if (RK_CORE_DEVICE.equals(prefix)) {
			return deviceIndex;
		} else if (RK_CORE2PA_UNDELIVERED.equals(prefix)) {
			return deviceIndex;
		} else if (RK_CORE2PA_UNDELIVERED.equals(prefix)) {
			return deviceIndex;
		} else {
			throw new RuntimeException("CoreMsgRoot does support " + prefix);
		}
	}
}
