package org.osgp.shared.dbs.perst;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.garret.perst.FieldIndex;
import org.osgp.util.dao.PK;

public class PerstUtils {

	//private static final Logger LOGGER = LoggerFactory.getLogger(PerstUtils.class);
	

	public static List<String> scanAll(final FieldIndex<? extends PbMsgPerst> index, final String prefix) {
		List<String> r = new ArrayList<>();
		Iterator<?> iter = index.iterator();
		while (iter.hasNext()) {
			PbMsgPerst perstMsg = (PbMsgPerst) iter.next();
			if (prefix == null || prefix.isEmpty() || perstMsg.strKey.startsWith(prefix)) {
				r.add(perstMsg.strKey);
			}
		}
		return r;
	}
	
	public static String correlIdFromKey(final PK pk) {
		return correlIdFromKey(pk.perstKey());
	}
	
	public static String correlIdFromKey(final String key) {
		if (key.indexOf(":") > 0) {
			return key.substring(key.indexOf(":")+1);
		} else {
			return null;
		}
	}

	public static List<PK> getAllPks(final FieldIndex<? extends PbMsgPerst> index) {
		return getAllPks(index, null);
	}
	
	public static List<PK> getAllPks(final FieldIndex<? extends PbMsgPerst> index, final String prefix) {
		List<PK> r = new ArrayList<>();
		Iterator<?> iter = index.iterator();
		while (iter.hasNext()) {
			PbMsgPerst perstMsg = (PbMsgPerst) iter.next();
			if (prefix == null || prefix.isEmpty() || perstMsg.strKey.startsWith(prefix)) {
				r.add(new PK(perstMsg.strKey));//TODO klopt dit voor aerospike ?
			}
		}
		return r;
	}


	private PerstUtils() {}
	
}
