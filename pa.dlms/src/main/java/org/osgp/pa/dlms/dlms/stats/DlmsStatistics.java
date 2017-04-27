package org.osgp.pa.dlms.dlms.stats;

import org.osgp.util.Statistics;

import com.alliander.osgp.shared.StatsMsg;

public class DlmsStatistics {

	private static Statistics statistics = new Statistics("dlms");
	
	private DlmsStatistics() {
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
	
	public static synchronized void incErrCount() {
		statistics.incErrorCount();
	}
	
	public static synchronized void incRetryCount() {
		statistics.incRetryCount();
	}
}
