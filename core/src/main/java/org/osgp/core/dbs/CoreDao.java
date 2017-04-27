package org.osgp.core.dbs;

import java.util.List;

import org.osgp.shared.dbs.UndeliveredTuple;
import org.osgp.util.dao.PK;

import com.alliander.osgp.shared.DeviceMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

public interface CoreDao {

	DeviceMsg findDevice(final RequestResponseMsg request);
	
	void saveReqRespToScheduleRepo(final RequestResponseMsg request);
	
	RequestResponseMsg findScheduledReqRespMsg(final String correlid);
	
	List<ScheduledTaskTuple> getAllScheduledTasks();
	
	List<PK> getAllScheduledTaskPks();
	
	void saveCoreDevice(final DeviceMsg deviceMsg);

	void saveCoreDevices(final List<DeviceMsg> deviceMsg);

	DeviceMsg getCoreDevice(final String deviceId);
	
	void saveUndeliveredRequest(final RequestResponseMsg reqRespMsg);
	
	List<UndeliveredTuple> getAllUndeliveredRequests();
	
	void delete(final PK pk);
	
//	Object get(PK pk);
	
	void commit();
}
