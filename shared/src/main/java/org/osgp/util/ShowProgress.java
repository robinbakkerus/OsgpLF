package org.osgp.util;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.HashMap;
import java.util.Map;


public class ShowProgress {

	private static Map<String, Counter> counters = new HashMap<>();
	
	public static void clock(final String name) {
		if (!counters.containsKey(name)) {
			counters.put(name, new Counter(name));
		}
		
		final Counter counter = counters.get(name);
		counter.inc();
		if (counter.count % 25000 == 0) {
			ShowProgress.show(counter);
		}
	}
	
	static void show(final Counter counter) {
		long seconds = NANOSECONDS.toSeconds(System.nanoTime() - counter.startTime);
		System.out.println(counter.name + " : " + counter.count + " in " + seconds + " sec.");
	}
	
	private static class Counter {
		final String name;
		final long startTime = System.nanoTime();
		int count = 0;
		
		Counter(final String name) {
			this.name = name;
		}
		
		void inc() {
			this.count++;
		}
	}
}



