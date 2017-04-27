package org.osgp.smint.rest.service;

import org.osgp.smint.service.AbstractService;

import com.alliander.osgp.dlms.HeStatistics;

public class StatisticsRestService extends AbstractService {


	public String getStatics() {
		StringBuilder sb = startJson();
		addJson(getStatistics(), sb);
		endJson(sb);
		return sb.toString();
	}

	public String resetStatics() {
		GetStatistics.getInstance().resetStatistics();
		return "OK";
	}

	private HeStatistics  getStatistics() {
		return GetStatistics.getInstance().getStatistics();
	}
	
}
