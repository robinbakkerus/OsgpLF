package org.osgp.smint.test;

import org.junit.Test;
import org.osgp.smint.rest.service.GetStatistics;

import com.alliander.osgp.dlms.HeStatistics;

public class TestGetStatistics {

	@Test
	public void test() {
		GetStatistics.getInstance().resetStatistics();
	}

	@Test
	public void test2() {
		HeStatistics stats = GetStatistics.getInstance().getStatistics();
		System.out.println(stats);
	}
}
