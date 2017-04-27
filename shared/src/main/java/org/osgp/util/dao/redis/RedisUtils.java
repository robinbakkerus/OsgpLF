package org.osgp.util.dao.redis;

public class RedisUtils {

	public static String correlIdFromKey(final byte[] bytes) {
		String s = new String(bytes);
		if (s.indexOf(":") > 0) {
			return s.substring(s.indexOf(":")+1);
		} else {
			return null;
		}
	}
	
	public static byte[] key(final String prefix, final String keyValue) {
		return (prefix + keyValue).getBytes();
	}

	public static byte[] scanKey(final String prefix) {
		return (prefix + "*").getBytes();
	}
	
	private RedisUtils() {
	}
}
