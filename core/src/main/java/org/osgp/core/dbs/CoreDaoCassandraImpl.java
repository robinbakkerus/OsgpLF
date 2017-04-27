package org.osgp.core.dbs;

import static org.osgp.core.dbs.CoreCassandraClient.FLD_CORREL_ID;
import static org.osgp.core.dbs.CoreCassandraClient.TABLE_CORE_DEVICE;
import static org.osgp.core.dbs.CoreCassandraClient.TABLE_CORE_SCHEDULE;
import static org.osgp.core.dbs.CoreCassandraClient.TABLE_UNDELIVERED_DLMS;
import static org.osgp.util.dao.cassandra.CassandraHelper.blob;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.osgp.shared.dbs.UndeliveredTuple;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.cassandra.CassandraHelper;

import com.alliander.osgp.shared.DeviceMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.datastax.driver.core.Session;

public class CoreDaoCassandraImpl implements CoreDao {


	final CassandraHelper<DeviceMsg> deviceHelper = 
			new CassandraHelper<>(CoreCassandraClient.getTableData(TABLE_CORE_DEVICE));
	final CassandraHelper<RequestResponseMsg> undelHelper = 
			new CassandraHelper<>(CoreCassandraClient.getTableData(TABLE_UNDELIVERED_DLMS));
	final CassandraHelper<RequestResponseMsg> schedHelper = 
			new CassandraHelper<>(CoreCassandraClient.getTableData(TABLE_CORE_SCHEDULE));
	
	@Override
	public DeviceMsg findDevice(RequestResponseMsg request) {
		return deviceHelper.select(session(), request.getCommon().getDeviceId());
	}

	@Override
	public void saveReqRespToScheduleRepo(RequestResponseMsg request) {
		schedHelper.save(session(), request.getCorrelId(), blob(request));
	}

	@Override
	public RequestResponseMsg findScheduledReqRespMsg(String correlid) {
		return schedHelper.select(session(), correlid);
	}

	@Override
	public List<ScheduledTaskTuple> getAllScheduledTasks() {
		List<RequestResponseMsg> allMsg = schedHelper.selectAll(session());
		return allMsg.stream().map(r -> new ScheduledTaskTuple(new PK(r.getCorrelId(), TABLE_CORE_SCHEDULE), r))
				.collect(Collectors.toList());
	}

	@Override
	public List<PK> getAllScheduledTaskPks() {
		List<RequestResponseMsg> allMsg = schedHelper.selectAll(session());
		return allMsg.stream().map(r -> new PK(r.getCorrelId(), TABLE_CORE_SCHEDULE))
				.collect(Collectors.toList());
	}

	@Override
	public void saveCoreDevice(DeviceMsg deviceMsg) {
		deviceHelper.save(session(), deviceMsg.getDeviceId(), blob(deviceMsg));
	}

	@Override
	public void saveCoreDevices(List<DeviceMsg> deviceMsgList) {
		List<List<Object>> valuesList = new ArrayList<>();
		deviceMsgList.stream().forEach(f -> valuesList.add(this.makeCoreDeviceObjectList(f)));
		deviceHelper.saveList(session(), valuesList);
	}
	
	private List<Object> makeCoreDeviceObjectList(final DeviceMsg deviceMsg) {
		List<Object> r = new ArrayList<>();
		r.add(deviceMsg.getDeviceId());
		r.add(blob(deviceMsg));
		return r;
	}
	
	@Override
	public DeviceMsg getCoreDevice(String deviceId) {
		return deviceHelper.select(session(), deviceId);
	}

	@Override
	public void saveUndeliveredRequest(RequestResponseMsg reqRespMsg) {
		undelHelper.save(session(), reqRespMsg.getCorrelId(), blob(reqRespMsg));
	}

	@Override
	public List<UndeliveredTuple> getAllUndeliveredRequests() {
		List<RequestResponseMsg> allMsg = undelHelper.selectAll(session());
		return allMsg.stream().map(r -> new UndeliveredTuple(new PK(r.getCorrelId(), TABLE_UNDELIVERED_DLMS), r))
				.collect(Collectors.toList());
	}

	@Override
	public void delete(PK pk) {
		String qry = String.format("DELETE FROM %s WHERE %s = ?", pk.getTable(), FLD_CORREL_ID);
		session().execute(qry, UUID.fromString(pk.getKey().toString()));
	}


	@Override
	public void commit() {
	}
	
	// --------------

	private static Session session() {
		return CoreDbsMgr.INSTANCE.dbsMgr().getCassandraSession();
	}
	

}
