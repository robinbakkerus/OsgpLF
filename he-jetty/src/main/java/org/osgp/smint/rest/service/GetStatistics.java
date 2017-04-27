package org.osgp.smint.rest.service;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.osgp.util.rpc.GrpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.HeStatistics;
import com.alliander.osgp.shared.AckMsg;
import com.alliander.osgp.shared.OsgpServiceGrpc;
import com.alliander.osgp.shared.RequestStatsMsg;
import com.alliander.osgp.shared.StatsMsg;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

public class GetStatistics {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetStatistics.class.getName());

	private final List<ManagedChannel> channels = new ArrayList<>();
	private final List<OsgpServiceGrpc.OsgpServiceBlockingStub> osgpStubs = new ArrayList<>();

	private static GetStatistics instance;

	public static GetStatistics getInstance() {
		if (instance == null) {
			instance = new GetStatistics();
		}
		return instance;
	}
	
	private GetStatistics() {
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

	public void resetStatistics() {
		for (int i = 0; i < 3; i++) {
			try {
				osgpStubs.get(i).initStatistics(AckMsg.newBuilder().build());
			} catch (StatusRuntimeException e) {
				LOGGER.warn("RPC failed: {0}", e.getStatus());
			}
		}
	}

	public HeStatistics getStatistics() {
		List<StatsMsg> statsMsgList = new ArrayList<>();
		
		RequestStatsMsg request = RequestStatsMsg.newBuilder().build();
		for (int i = 0; i < 3; i++) {
			try {
				statsMsgList.add(osgpStubs.get(i).getStatistics(request));
			} catch (StatusRuntimeException e) {
				LOGGER.warn("RPC failed: {0}", e.getStatus());
				statsMsgList.add(StatsMsg.newBuilder().build());
			}
		}
		return fillSummaryCounts(statsMsgList);
	}
	
	private HeStatistics fillSummaryCounts(final List<StatsMsg> statMsgList) {
		Long resetTime = NANOSECONDS.toSeconds(System.nanoTime() - statMsgList.get(2).getResetTime());
		return HeStatistics.newBuilder()
				.addAllStats(statMsgList)
				.setErrorCount(statMsgList.get(2).getErrCount())
				.setRetryCount(statMsgList.get(1).getRetryCount())
				.setTimeSinceReset(resetTime.intValue())
				.build();
	}
	
}
