package org.osgp.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ProtocolHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolHelper.class.getName());

	private static Map<String, Config> protocolConfigs = new HashMap<>();
	
	private ProtocolHelper() {
	}
	
	public static void initialize() {
		Config config = ConfigFactory.load("core");
		List<String> protocolComponents = config.getStringList("protocol.components");
		for (String prot : protocolComponents) {
			String protConfile = "protocol." + prot;
			LOGGER.info("loading config for protocol " + prot + " from " + protConfile);
			protocolConfigs.put(prot, ConfigFactory.load(protConfile));
		}
	}
	

	public static String getStrValue(final String protocol, final String path, final String defaultValue) {
		if (protocolConfigs.containsKey(protocol)) {
			final Config cfg = protocolConfigs.get(protocol);
			if (cfg.hasPath(path)) {
				return cfg.getString(path);
			} else {
				return defaultValue;
			}
		} else {
			throw new RuntimeException("protocol config does not exits for " + protocol);
		}
	}
	
	public static String getStrValue(final String protocol, final String path) {
		if (protocolConfigs.containsKey(protocol)) {
			final Config cfg = protocolConfigs.get(protocol);
			if (cfg.hasPath(path)) {
				return cfg.getString(path);
			} else {
				throw new RuntimeException("protocol config " + protocol + " does not contain " + path);
			}
		} else {
			throw new RuntimeException("protocol " + protocol + " does not exist for ");
		}
	}
	
	public static int getIntValue(final String protocol, final String path) {
		if (protocolConfigs.containsKey(protocol)) {
			final Config cfg = protocolConfigs.get(protocol);
			if (cfg.hasPath(path)) {
				return cfg.getInt(path);
			} else {
				throw new RuntimeException("protocol config " + protocol + " does not contain " + path);
			}
		} else {
			throw new RuntimeException("protocol " + protocol + " does not exist for ");
		}
	}
	
	private static String PROTOCOL_DLMS = "dlms"; //TODO this should be dynamic ! 
	

	public static String getRpcClientHost() {
		final String path = "protocol-adapter.grpc.host";
		return ProtocolHelper.getStrValue(PROTOCOL_DLMS, path);
	}
	
	public static int getRpcClientPort() {
		final String path = "protocol-adapter.grpc.host";
		return ProtocolHelper.getIntValue(PROTOCOL_DLMS, path);
	}
}
