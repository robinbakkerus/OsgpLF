package org.osgp.pa.dlms.application.dao.perst;

import org.osgp.shared.dbs.perst.AbstractPerstStorage;

public class DlmsPerstStorage extends AbstractPerstStorage<DlmsPerstRoot>{

	public DlmsPerstStorage(String dbsName) {
		super(dbsName);
	}

	@Override
	protected DlmsPerstRoot makeRoot() {
		System.out.println("DlmsPerstStorage.makeRoot");
		return new DlmsPerstRoot(getStorage());
	}
	
	

}
