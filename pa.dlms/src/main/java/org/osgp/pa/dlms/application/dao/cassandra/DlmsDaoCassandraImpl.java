package org.osgp.pa.dlms.application.dao.cassandra;

import static org.osgp.pa.dlms.application.dao.cassandra.DlmsCassandraClient.TBL_DLMS_DEVICE;
import static org.osgp.util.dao.cassandra.CassandraHelper.blob;

import java.util.ArrayList;
import java.util.List;

import org.osgp.pa.dlms.application.dao.DlmsDao;
import org.osgp.pa.dlms.application.dao.DlmsDbsMgr;
import org.osgp.shared.exceptionhandling.FunctionalException;
import org.osgp.util.dao.PK;
import org.osgp.util.dao.cassandra.CassandraHelper;

import com.alliander.osgp.dlms.DlmsDeviceMsg;
import com.datastax.driver.core.Session;

public class DlmsDaoCassandraImpl implements DlmsDao {

	final CassandraHelper<DlmsDeviceMsg> devHelper = 
			new CassandraHelper<>(DlmsCassandraClient.getTableData(TBL_DLMS_DEVICE));
	
	@Override
	public DlmsDeviceMsg findByDeviceId(String deviceId) throws FunctionalException {
		return devHelper.select(session(), deviceId);
	}

	@Override
	public DlmsDeviceMsg save(DlmsDeviceMsg dlmsDevice) {
		devHelper.save(session(), dlmsDevice.getIdentification(), blob(dlmsDevice));
		return dlmsDevice;
	}
	
	@Override
	public void saveList(List<DlmsDeviceMsg> dlmsDeviceList) {
		List<List<Object>> valuesList = new ArrayList<>();
		dlmsDeviceList.stream().forEach(f -> valuesList.add(this.makeDlmsDeviceObjectList(f)));
		devHelper.saveList(session(), valuesList);
	}
	
	private List<Object> makeDlmsDeviceObjectList(final DlmsDeviceMsg deviceMsg) {
		if (deviceMsg != null) {
			List<Object> r = new ArrayList<>();
			r.add(deviceMsg.getIdentification());
			r.add(blob(deviceMsg));
			return r;
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public Object get(PK pk) {
		return null;
	}

	@Override
	public void commit() {
	}

	// --------------


	private static Session session() {
		return DlmsDbsMgr.INSTANCE.dbsMgr().getCassandraSession();
	}
	
}
