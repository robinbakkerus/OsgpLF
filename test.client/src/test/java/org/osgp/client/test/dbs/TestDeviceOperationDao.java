package org.osgp.client.test.dbs;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.osgp.client.dao.ClientDao;
import org.osgp.client.dao.ClientDaoFact;
import org.osgp.client.dao.ClientDbsMgr;
import org.osgp.client.dao.DeviceOperationTuple;
import org.osgp.shared.dbs.Database;
import org.osgp.util.SystemPropertyHelper;
import org.osgp.util.dao.PK;

import com.alliander.osgp.dlms.DlmsDevOperMsg;
import com.alliander.osgp.shared.CommonMsg;

public class TestDeviceOperationDao {

	private static final String DEVID = "DEV" + new Date().getTime();

	@After
	public void afterEach() {
		ClientDbsMgr.INSTANCE.close();
		SystemPropertyHelper.clearProperties();
	}

	@Test
	public void testPerstDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.PERST);
		testDao();
	}

	@Test
	public void testRedisDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.Redis);
		testDao();
	}

	@Test
	public void testAerospikeDeviceDao() {
		SystemPropertyHelper.setupDatabase(Database.Aerospike);
		testDao();
	}

	private void testDao() {
		ClientDbsMgr.INSTANCE.open();
		ClientDaoFact.INSTANCE.reset();

		DlmsDevOperMsg devopMsg1 = DlmsDevOperMsg.newBuilder()
				.setCommon(CommonMsg.newBuilder().setDeviceId(DEVID).build()).build();
		dao().saveDeviceOperation(devopMsg1);
		List<DeviceOperationTuple> allDevops = dao().getAllDeviceOperations();
		Assert.assertTrue(allDevops.size() > 0);
		List<DeviceOperationTuple> filtlist = allDevops.stream()
				.filter(t -> t.getDeviceOperation().getCommon().getDeviceId().equals(DEVID)).collect(Collectors.toList());
		
		Assert.assertTrue(filtlist.size() == 1);
		PK pk = filtlist.get(0).getPk();
		dao().delete(pk);
		allDevops = dao().getAllDeviceOperations();
		Assert.assertFalse(allDevops.stream().anyMatch(t -> t.getPk().equals(pk)));
	}

	private ClientDao dao() {
		return ClientDaoFact.INSTANCE.getDao();
	}
}
