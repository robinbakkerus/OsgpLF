package org.osgp.pa.dlms.application.dao.perst;

import org.garret.perst.FieldIndex;
import org.garret.perst.Persistent;
import org.garret.perst.Storage;
import org.osgp.shared.CC;

public class DlmsPerstRoot extends Persistent implements CC {

	public FieldIndex<PerstDlmsDeviceMsg> dlmsDeviceIndex;

	public DlmsPerstRoot(Storage storage) {
		super(storage);
		dlmsDeviceIndex = storage.<PerstDlmsDeviceMsg> createFieldIndex(PerstDlmsDeviceMsg.class, "strKey", true);
	}
	
}
