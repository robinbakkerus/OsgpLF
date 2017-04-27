package org.osgp.util.dao.perst;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.garret.perst.FieldIndex;
import org.osgp.shared.dbs.perst.PbMsgPerst;
import org.osgp.shared.dbs.perst.PerstUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.GeneratedMessageV3;

public class PerstSelectAllHelper<T extends PbMsgPerst, M extends GeneratedMessageV3> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PerstSelectAllHelper.class);
	
	public List<M> selectAll(final FieldIndex<T> index, final Class<? extends GeneratedMessageV3> clazz) {
		return selectAll(index, clazz, null);
	}

	@SuppressWarnings("unchecked")
	public List<M> selectAll(final FieldIndex<T> index, final Class<? extends GeneratedMessageV3> clazz, final String prefix) {
		List<M> r = new ArrayList<>();
		for (String key : PerstUtils.scanAll(index, prefix)) {
			try {
				Method m = clazz.getDeclaredMethod("parseFrom", byte[].class);
				r.add((M) m.invoke(null, getBytes(index, key)));
			} catch (Exception e) {
				LOGGER.error("error parsing record for class" + clazz.getName() + " :" + e);
			}
		}
		return r;
	}

	private byte[] getBytes(final FieldIndex<?> index, final String key) {
		PbMsgPerst msg = (PbMsgPerst) index.get(key);
		return msg == null ? null : msg.body;
	}
	
}
