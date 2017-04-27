package org.osgp.client.dao.perst;

import org.osgp.shared.dbs.perst.AbstractPerstStorage;

public class ClientPerstStorage extends AbstractPerstStorage<ClientPerstRoot>{

	public ClientPerstStorage(String dbsName) {
		super(dbsName);
	}

	@Override
	protected ClientPerstRoot makeRoot() {
		System.out.println("DlmsPerstStorage.makeRoot");
		return new ClientPerstRoot(getStorage());
	}
	
	

}
