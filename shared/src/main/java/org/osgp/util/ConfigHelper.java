package org.osgp.util;

import java.util.ArrayList;
import java.util.List;

import org.osgp.shared.dbs.Database;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;

public class ConfigHelper {

	public static List<Server> getServers(final Config config, final String path) {
		List<Server> servers = new ArrayList<>();
		List<? extends ConfigObject> configList = config.getObjectList(path);
		for (ConfigObject configObject : configList) {
			String host = configObject.get("host").atKey("host").getString("host");
			int port = configObject.get("port").atKey("port").getInt("port");
			String rpc = configObject.get("rpc").atKey("rpc").getString("rpc");
			servers.add(new Server(host, port, rpc));
		}

		return servers;
	}

	public static Database getDatabaseImpl() {
		ConfigFactory.invalidateCaches();
		Config config = ConfigFactory.load("common");
		String dbs = config.getString("osgp.database");
		if ("redis".equals(dbs.toLowerCase())) {
			return Database.Redis;
		} else if ("perst".equals(dbs.toLowerCase())) {
			return Database.PERST;
		} else if ("cassandra".equals(dbs.toLowerCase())) {
			return Database.CASSANDRA;
		} else {
			return Database.Aerospike;
		}
	}
}
