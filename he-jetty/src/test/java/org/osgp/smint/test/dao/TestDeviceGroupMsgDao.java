package org.osgp.smint.test.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgp.smint.dao.SmIntDao;
import org.osgp.smint.dao.SmIntDaoFact;
import org.osgp.smint.dao.SmIntDbsMgr;

import com.alliander.osgp.dlms.DeviceGroupMsg;

public class TestDeviceGroupMsgDao {

//	private static final Logger LOGGER = LoggerFactory.getLogger(TestDeviceGroupMsgDao.class.getName());

	@BeforeClass
	public static void before() {
		SmIntDbsMgr.INSTANCE.open();
	}
	
	@AfterClass
	public static void after() {
		System.out.println("after");
		SmIntDbsMgr.INSTANCE.close();
	}
	

	@Test
	public void testSaveDeviceGroupMsg() throws IOException {
		DeviceGroupMsg devGroups = makeSingleDeviceGroupMsg();
		dao().saveDeviceGroup(devGroups);
		
		List<DeviceGroupMsg> devGroupList = dao().getDeviceGroups();
		Assert.assertTrue(devGroupList.size() > 0);
	}
	
	@Test
	public void test1KDeviceGroupMsg() throws IOException {
		DeviceGroupMsg devGroups = make1KDeviceGroupMsg();
		dao().saveDeviceGroup(devGroups);
		
		List<DeviceGroupMsg> devGroupList = dao().getDeviceGroups();
		Assert.assertTrue(devGroupList.size() > 0);
	}	

	private DeviceGroupMsg makeSingleDeviceGroupMsg() {
		return DeviceGroupMsg.newBuilder()
				.setName("G1")
				.setDescription("Group with 1 device")
				.setCreationTime(now())
				.addDeviceIds("DEV1")
				.build();
	}

	private DeviceGroupMsg make1KDeviceGroupMsg() {
		return DeviceGroupMsg.newBuilder()
				.setName("G1K")
				.setDescription("Group with 1K devices")
				.setCreationTime(now())
				.addAllDeviceIds(make1KDeviceIds())
				.build();
	}

	private List<String> make1KDeviceIds() {
		List<String> r = new ArrayList<>();
		for (int i=1; i<=1000; i++) {
			r.add("DEV" + i);
		}
		return r;
	}
	
	private SmIntDao dao() {
		return SmIntDaoFact.INSTANCE.getDao();
	}

	private long now() {
		return new Date().getTime();
	}
}
