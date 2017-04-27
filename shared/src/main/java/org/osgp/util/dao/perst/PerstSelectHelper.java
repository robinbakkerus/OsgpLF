package org.osgp.util.dao.perst;

import java.lang.reflect.Method;

import org.garret.perst.FieldIndex;
import org.osgp.shared.dbs.perst.PbMsgPerst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.GeneratedMessageV3;

public class PerstSelectHelper<T extends PbMsgPerst, M extends GeneratedMessageV3> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PerstSelectHelper.class);
	
	@SuppressWarnings("unchecked")
	public M select(final FieldIndex<T> index, final Class<? extends GeneratedMessageV3> clazz, final String key) {
		
		byte[] bytes = getBytes(index, key);
		if (bytes != null) {
			try {
				Method m = clazz.getDeclaredMethod("parseFrom", byte[].class);
				return (M) m.invoke(null, (byte[]) bytes);
			} catch (Exception e) {
				LOGGER.error("error parsing record " + e);
				return null;
			}
		} else {
			return null;
		}
	}		

	private byte[] getBytes(final FieldIndex<?> index, final String key) {
		PbMsgPerst msg = (PbMsgPerst) index.get(key);
		return msg == null ? null : msg.body;
	}
	
}
