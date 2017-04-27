package org.osgp.util;

import java.util.HashSet;
import java.util.Set;

import org.osgp.shared.dbs.Database;

public class SystemPropertyHelper {

	private static Set<String> keys = new HashSet<>();
	
	public static void setProperty(String key, String value) {
		keys.add(key);
		System.setProperty(key, value);
	}

	/**
	 * This method accepts input like: "useStub=true;fail=0.0"
	 * @param keyAndValues
	 */
	public static void setProperties(final String keyAndValues) {
		if (keyAndValues.contains("=")) {
			String keyvaluePairs[] = keyAndValues.split(";");
			for (String keyvalue : keyvaluePairs) {
				String tokens[] = keyvalue.split("=");
				if (tokens.length>0) {
					setProperty(tokens[0], tokens[1]);
				}
			}
		}
	}
	
	
	public static void setupDatabase(Database dbtype) {
		keys.add("osgp.database");
		if (Database.Aerospike == dbtype) {
			System.setProperty("osgp.database", "aerospike");
		} else if (Database.Redis == dbtype) {
			System.setProperty("osgp.database", "redis");
		} else if (Database.CASSANDRA == dbtype) {
			System.setProperty("osgp.database", "cassandra");
		} else {
			System.setProperty("osgp.database", "perst");
		}
		
		System.out.println(System.getProperty("osgp.database"));
	}
	
	public static void clearProperties() {
		for (String key : keys) {
			System.clearProperty(key);
		}
		keys.clear();
	}
}
