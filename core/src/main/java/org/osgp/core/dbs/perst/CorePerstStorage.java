package org.osgp.core.dbs.perst;

import org.osgp.shared.dbs.perst.AbstractPerstStorage;

public class CorePerstStorage extends AbstractPerstStorage<CorePerstRoot>{

	public CorePerstStorage(String dbsName) {
		super(dbsName);
	}

	@Override
	protected CorePerstRoot makeRoot() {
		System.out.println("CorePerstRoot.makeRoot");
		return new CorePerstRoot(getStorage());
	}
	
	

}
