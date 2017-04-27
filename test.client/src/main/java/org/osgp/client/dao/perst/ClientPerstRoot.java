package org.osgp.client.dao.perst;

import org.garret.perst.FieldIndex;
import org.garret.perst.Persistent;
import org.garret.perst.Storage;
import org.osgp.shared.CC;

public class ClientPerstRoot extends Persistent implements CC {

	public FieldIndex<PerstDevOperMsg> devOperIndex;
	public FieldIndex<PerstBundleMsg> bundleIndex;

	public ClientPerstRoot(Storage storage) {
		super(storage);
		devOperIndex = storage.<PerstDevOperMsg> createFieldIndex(PerstDevOperMsg.class, "strKey", true);
		bundleIndex = storage.<PerstBundleMsg> createFieldIndex(PerstBundleMsg.class, "strKey", true);
	}
	
}
