package org.osgp.platform.stats;

import org.osgp.util.Statistics;

import com.alliander.osgp.shared.StatsMsg;

public class PlatformStatistics {

	private static Statistics statistics = new Statistics("platform");
	
	private PlatformStatistics() {
	}
	
	public static void incRequestsIn() {
		statistics.incRequestsIn();
	}
	
	public static void incRequestsOut() {
		statistics.incRequestsOut();
	}
	
	public static void incResponsesIn() {
		statistics.incResponsesIn();
	}
	
	public static void incResponsesOut() {
		statistics.incResponsesOut();
	}
	
	public static StatsMsg toStatsMsg() {
		return statistics.toStatsMsg();
	}
	
	public static void reset() {
		statistics.reset();
	}
}
