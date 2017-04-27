package org.osgp.util.dao.perst;

import org.garret.perst.FieldIndex;
import org.osgp.shared.dbs.perst.PbMsgPerst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerstSaveHelper<T extends PbMsgPerst> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PerstSaveHelper.class);
	
	public void save(final FieldIndex<T> index, T perstMsg) {
		try {
			if (index.get(perstMsg.strKey) != null) {
				index.remove(perstMsg);
			}
			index.put((T) perstMsg);
		} catch (Exception ex) {
			LOGGER.error("error while saving " + perstMsg + " in " + ex);
		}
	}
}
