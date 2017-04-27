package org.osgp.shared.dbs.perst;

import org.garret.perst.Storage;
import org.garret.perst.StorageFactory;

public abstract class AbstractPerstStorage<T> {

	protected String dbsName;
	protected T root;
	private Storage storage;
	
	public AbstractPerstStorage(String dbsName) {
		super();
		this.dbsName = dbsName;
		this.initializeRoot();
	}

	static int pagePoolSize = 32*1024*1024;

	@SuppressWarnings("unchecked")
	private void initializeRoot() {
		if (root == null) {
			storage = StorageFactory.getInstance().createStorage();
			storage.open(dbsName);

			root = (T) storage.getRoot();
			if (root == null) {
				root = makeRoot(); 
				storage.setRoot(root); 
			}
		}
	}
	
	protected abstract T makeRoot();
	
	public void shutdown() {
		if (storage != null) {
			storage.close();
		}
	}
	
	protected Storage getStorage() {
		return this.storage;
	}


	public T getMsgRoot() {
		return root;
	}
	
	
}
