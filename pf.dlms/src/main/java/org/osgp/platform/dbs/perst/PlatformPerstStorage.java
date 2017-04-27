package org.osgp.platform.dbs.perst;

import org.osgp.shared.dbs.perst.AbstractPerstStorage;

public class PlatformPerstStorage extends AbstractPerstStorage<PlatformPerstRoot>{

	public PlatformPerstStorage(String dbsName) {
		super(dbsName);
	}

	@Override
	protected PlatformPerstRoot makeRoot() {
		System.out.println("PlatformPerstRoot.makeRoot");
		return new PlatformPerstRoot(getStorage());
	}
	
	

}
