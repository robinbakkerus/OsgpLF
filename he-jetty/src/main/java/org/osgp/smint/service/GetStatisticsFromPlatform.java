package org.osgp.smint.service;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.osgp.util.rpc.GrpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.RequestStatsMsg;
import com.alliander.osgp.shared.StatsMsg;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

public class GetStatisticsFromPlatform {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetStatisticsFromPlatform.class.getName());

	private final List<ManagedChannel> channels = new ArrayList<>();
	private final List<OsgpServiceGrpc.OsgpServiceBlockingStub> osgpStubs = new ArrayList<>();

	public GetStatisticsFromPlatform() {
		for (int i = 0; i < 3; i++) {
			try {
				ManagedChannel channel = GrpcUtils.makeChannel(50051 + i);
				channels.add(channel);
				osgpStubs.add(OsgpServiceGrpc.newBlockingStub(channel));
			} catch (CertificateException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void shutdown() throws InterruptedException {
		for (ManagedChannel channel : channels) {
			channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
		}
	}

	
	public List<StatsMsg>  getStatistics() {
		List<StatsMsg> result = new ArrayList<>();
		RequestStatsMsg request = RequestStatsMsg.newBuilder().build();
		for (int i = 0; i < 3; i++) {
			try {
				result.add(osgpStubs.get(i).getStatistics(request));
			} catch (StatusRuntimeException e) {
				LOGGER.warn("RPC failed: {0}", e.getStatus());
			}
		}
		return result;
	}

	public void resetStatistics() {
		for (int i = 0; i < 3; i++) {
			try {
				osgpStubs.get(i).initStatistics(AckMsg.newBuilder().build());
			} catch (StatusRuntimeException e) {
				LOGGER.warn("RPC failed: {0}", e.getStatus());
			}
		}

	}

//	private String getSummary(RequestStatsMsg request) {
//		String finishedIn = "not yet ...";
//		long ttime = 0L;
//		try {
//			StatsMsg statMsg = osgpStubs.get(0).getStatistics(request);
//			ttime = NANOSECONDS.toSeconds(System.nanoTime() - statMsg.getResetTime());
//
//			if (statMsg.getResponses().getInCount() >= statMsg.getRequests().getInCount()) {
//				long ftime = NANOSECONDS
//						.toSeconds(statMsg.getResponses().getLastInTimestamp() - statMsg.getResetTime());
//				finishedIn = "in " + ftime;
//			}
//
//		} catch (StatusRuntimeException e) {
//		}
//		return "since reset : " + ttime + " finished: " + finishedIn + NL;
//	}
//
//	private String getStats(StatsMsg statMsg) {
//		StringBuffer sb = new StringBuffer();
//		sb.append(statMsg.getName() + NL);
//		sb.append("\tcount \ttime \tlap" + NL);
//		sb.append(getPrintDetails("req", statMsg.getRequests()) + NL);
//		sb.append(getPrintDetails("res", statMsg.getResponses()) + NL);
//		return sb.toString();
//	}
//
//	private String getPrintDetails(String prefix, StatDetailsMsg det) {
//		StringBuffer sb = new StringBuffer();
//		String label = prefix + "-i";
//		long ttime = NANOSECONDS.toSeconds(det.getLastInTimestamp() - det.getFirstInTimestamp());
//		long lap = NANOSECONDS.toMillis(det.getLastInTimestamp() - det.getPrevInTimestamp());
//		sb.append(String.format("%s \t%d \t%d \t%d", label, det.getInCount(), ttime, lap) + NL);
//		label = prefix + "-o";
//		ttime = NANOSECONDS.toSeconds(det.getLastOutTimestamp() - det.getFirstOutTimestamp());
//		lap = NANOSECONDS.toMillis(det.getLastOutTimestamp() - det.getPrevOutTimestamp());
//		sb.append(String.format("%s \t%d \t%d \t%d", label, det.getOutCount(), ttime, lap) + NL);
//		return sb.toString();
//	}


}
