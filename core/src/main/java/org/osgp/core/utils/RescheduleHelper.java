package org.osgp.core.utils;

import com.alliander.osgp.shared.RequestResponseMsg;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class RescheduleHelper  {

	//private static final Logger LOGGER = LoggerFactory.getLogger(RescheduleHelper.class.getName());

	private static Config config = ConfigFactory.load("core");

	private RescheduleHelper() {}
	
	public static boolean shouldBeRetried(RequestResponseMsg msg) {
		return msg.getCommon().getRetry() && msg.getCommon().getRetryCount() < maxRetryCount();
	}

	public static long calcScheduleTime(RequestResponseMsg msg) {
		final long now = System.currentTimeMillis();
		int retrycnt = msg.getCommon().getRetryCount();
		long waitFor = (int) (laptime() * Math.pow(2.0, retrycnt));
		return now + waitFor;
	}

	private static int maxRetryCount() {
		String path = "retry.max";
		if (config.hasPath(path)) {
			return config.getInt(path);
		} else {
			return 4;
		}
	}

	private static int laptime() {
		String path = "retry.laptime";
		if (config.hasPath(path)) {
			return config.getInt(path);
		} else {
			return 1000 * 60; //todo
		}
	}

}
