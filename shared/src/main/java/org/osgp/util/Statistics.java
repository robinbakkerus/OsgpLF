package org.osgp.util;

import java.util.Stack;

import com.alliander.osgp.shared.StatDetailsMsg;
import com.alliander.osgp.shared.StatsMsg;

public class Statistics {

	private String name = "unknown";
	private long resetTime = System.nanoTime();
	private int prevCount = 1000;//default
	private int errCount = 0;
	private int retryCount = 0;

	
	private StatisticDetails requests = new StatisticDetails();
	private StatisticDetails responses = new StatisticDetails();
	
	public Statistics(String name) {
		this.name = name;
	}

	public void incRequestsIn() {
		requests.incrementIn();
	}
	
	public void incRequestsOut() {
		requests.incrementOut();
	}
	
	public void incResponsesIn() {
		responses.incrementIn();
	}
	
	public void incResponsesOut() {
		responses.incrementOut();
	}
	
	public void incErrorCount() {
		this.errCount++;
	}

	public void incRetryCount() {
		this.retryCount++;
	}

	
	public void reset() {
		resetTime = System.nanoTime();
		requests.reset();
		responses.reset();
	}
	
	public StatsMsg toStatsMsg() {
		return StatsMsg.newBuilder()
				.setResetTime(this.resetTime)
				.setName(this.name)
				.setRequests(this.requests.toStatDetailsMsg())
				.setResponses(this.responses.toStatDetailsMsg())
				.setErrCount(this.errCount)
				.setRetryCount(this.retryCount)
				.build();
	}
	
	public String getName() {
		return name;
	}

	public long getResetTime() {
		return resetTime;
	}


	private class StatisticDetails {
		private long firstInTimestamp = 0L;
		private long lastInTimestamp = 0L;
		private int inCount = 0;
		private long firstOutTimestamp = 0L;
		private long lastOutTimestamp = 0L;
		private int outCount = 0;
		
		SizedStack<Long> timestampsIn = new SizedStack<Long>(prevCount);
		SizedStack<Long> timestampsOut = new SizedStack<Long>(prevCount);
		
		void incrementIn() {
			long now = System.nanoTime();
			if (firstInTimestamp == 0L) firstInTimestamp = now;
			lastInTimestamp = now;
			timestampsIn.push(now);
			inCount++;
		}
		
		void incrementOut() {
			long now = System.nanoTime();
			if (firstOutTimestamp == 0L) firstOutTimestamp = now;
			lastOutTimestamp = now;
			timestampsOut.push(now);
			outCount++;
		}
		
		void reset() {
			firstInTimestamp = 0L;
			lastInTimestamp = 0L;
			inCount = 0;
			firstOutTimestamp = 0L;
			lastOutTimestamp = 0L;
			outCount = 0;
			timestampsIn.clear();
			timestampsOut.clear();
			errCount = 0;
			retryCount = 0;
		}
		
		public StatDetailsMsg toStatDetailsMsg() {
			long prevIn = timestampsIn.size()>0 ? timestampsIn.firstElement() : firstInTimestamp;
			long prevOut = timestampsOut.size()>0 ? timestampsOut.firstElement() : firstOutTimestamp;

			return StatDetailsMsg.newBuilder()
					.setFirstInTimestamp(firstInTimestamp)
					.setLastInTimestamp(lastInTimestamp)
					.setPrevInTimestamp(prevIn)
					.setInCount(inCount)
					.setFirstOutTimestamp(firstOutTimestamp)
					.setLastOutTimestamp(lastOutTimestamp)
					.setPrevOutTimestamp(prevOut)
					.setOutCount(outCount)
					.build();
		}
	}
	
	@SuppressWarnings("serial")
	private class SizedStack<T> extends Stack<T> {
		private int maxSize;

	    public SizedStack(int size) {
	        super();
	        this.maxSize = size;
	    }

	    @Override
	    public T push(T object) {
	        while (this.size() >= maxSize) {
	            this.remove(0);
	        }
	        return super.push((T) object);
	    }
	}
}
