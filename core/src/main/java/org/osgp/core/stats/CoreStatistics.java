package org.osgp.core.stats;

import org.osgp.util.Statistics;

import com.alliander.osgp.shared.StatsMsg;

public class CoreStatistics {

	private static Statistics statistics = new Statistics("core");
	
	private CoreStatistics() {
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
	
	public static void incErrCount() {
		statistics.incErrorCount();
	}
	
	public static void incRetryCount() {
		statistics.incRetryCount();
	}

}
