package org.osgp.smint.rest.service;

import java.util.List;

import org.osgp.smint.dao.SmIntDao;
import org.osgp.smint.dao.SmIntDaoFact;
import org.osgp.smint.service.AbstractService;

import com.alliander.osgp.dlms.DeviceGroupMsg;

public class DeviceGroupRestService extends AbstractService {


	public String getAllDeviceGroups() {
		StringBuilder sb = startJsonArray();
		
		List<DeviceGroupMsg> allMsg = dao().getDeviceGroups();
		int i=0;
		for (DeviceGroupMsg msg : allMsg) {
			addJson(msg, sb);
			if (i++ < allMsg.size()-1) sb.append(",");
		}
		return endJsonArray(sb);
	}

	public String getDeviceGroup(final long groupId) {
		StringBuilder sb = startJson();
		DeviceGroupMsg msg = dao().getDeviceGroup(groupId);
		addJson(msg, sb);
		return endJson(sb);
	}

	//---------
	
	private SmIntDao dao() {
		return SmIntDaoFact.INSTANCE.getDao();
	}
	
}
