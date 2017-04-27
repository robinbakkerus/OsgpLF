package org.osgp.smint.dao.perst;

import org.osgp.shared.dbs.perst.AbstractPerstStorage;

public class SmIntPerstStorage extends AbstractPerstStorage<SmIntPerstRoot>{

	public SmIntPerstStorage(String dbsName) {
		super(dbsName);
	}

	@Override
	protected SmIntPerstRoot makeRoot() {
		System.out.println("DlmsPerstStorage.makeRoot");
		return new SmIntPerstRoot(getStorage());
	}
	
	

}
