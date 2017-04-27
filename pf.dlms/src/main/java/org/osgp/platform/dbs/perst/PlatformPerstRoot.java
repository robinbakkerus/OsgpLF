package org.osgp.platform.dbs.perst;

import org.garret.perst.FieldIndex;
import org.garret.perst.Persistent;
import org.garret.perst.Storage;
import org.osgp.shared.CC;

public class PlatformPerstRoot extends Persistent implements CC {

	public FieldIndex<PerstReqRespMsg> reqRespIndex;
	public FieldIndex<PerstReqRespMsg> undeliveredIndex;

	public PlatformPerstRoot(Storage storage) {
		super(storage);
		reqRespIndex = storage.<PerstReqRespMsg> createFieldIndex(PerstReqRespMsg.class, "strKey", true);
		undeliveredIndex = storage.<PerstReqRespMsg> createFieldIndex(PerstReqRespMsg.class, "strKey", true);
	}
	
}
