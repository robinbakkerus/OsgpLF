package org.osgp.client.setup;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.osgp.core.dbs.CoreDaoFact;
import org.osgp.core.dbs.CoreDbsMgr;
import org.osgp.dlms.DC;
import org.osgp.pa.dlms.application.dao.DlmsDaoFact;
import org.osgp.pa.dlms.application.dao.DlmsDbsMgr;
import org.osgp.pa.dlms.util.DlmsDeviceMsgBuildHelper;
import org.osgp.shared.CC;
import org.osgp.shared.exceptionhandling.FunctionalException;
import org.osgp.util.SystemPropertyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DlmsDeviceMsg;
import com.alliander.osgp.shared.DeviceMsg;
import com.google.common.collect.Lists;

public class InsertDevices {

	private static final Logger LOGGER = LoggerFactory.getLogger(InsertDevices.class.getName());

	private static int MAX = askInsertTotal();

	long startedAt = System.nanoTime();

	public static void main(String[] args) {
		try {
			SystemPropertyHelper.clearProperties();
//			ClientDbsMgr.INSTANCE.open();
			CoreDbsMgr.INSTANCE.open();
			DlmsDbsMgr.INSTANCE.open();
			InsertDevices mainclz = new InsertDevices();
			mainclz.insertCoreDevices();
			mainclz.insertDlmsDevices();
			mainclz.findCoreDevice();
			mainclz.findDlmsDevice();
			System.out.println("\nthe end ...");
		} finally {
//			ClientDbsMgr.INSTANCE.close();
			CoreDbsMgr.INSTANCE.close();
			DlmsDbsMgr.INSTANCE.close();
		}
	}

	private void insertCoreDevices() {
		LOGGER.info("start inserting devices every tick = 50k");
		long startTime = System.nanoTime();

		List<DeviceMsg> deviceList = new ArrayList<>();
		
		for (int i = 1; i <= MAX; i++) {
			deviceList.add(makeDeviceMsg(CC.PREFIX_DEVICE + i));
		}
		List<List<DeviceMsg>> partLists = Lists.partition(deviceList, 50000); 
		partLists.forEach(this::insertCoreDeviceList);
		
		CoreDaoFact.INSTANCE.getDao().commit();
		long seconds = NANOSECONDS.toMillis(System.nanoTime() - startTime);
		LOGGER.warn("inserted " + MAX + " core devices in " + seconds + " msecs");
	}

	private void insertCoreDeviceList(List<DeviceMsg> deviceList) {
		CoreDaoFact.INSTANCE.getDao().saveCoreDevices(deviceList);
		System.out.print(".");
	}

	private void insertDlmsDevices() {
		LOGGER.info("start inserting dlms devices every tick = 50k");
		
		List<DlmsDeviceMsg> dlmsDeviceList = new ArrayList<>();
		
		long startTime = System.nanoTime();
		for (int i = 1; i < MAX; i++) {
			String devid = DC.PREFIX_DEVICE + i;
			dlmsDeviceList.add(DlmsDeviceMsgBuildHelper.makeDlmsDeviceMsg(devid));
		}

		List<List<DlmsDeviceMsg>> partLists = Lists.partition(dlmsDeviceList, 50000); 
		partLists.forEach(this::insertDlmsDeviceList);
		
		DlmsDaoFact.INSTANCE.getDao().commit();
		long seconds = NANOSECONDS.toMillis(System.nanoTime() - startTime);
		LOGGER.warn("inserted " + MAX + " dlms devices in " + seconds + " msecs");
	}

	private void insertDlmsDeviceList(final List<DlmsDeviceMsg> dlmsDeviceList) {
		DlmsDaoFact.INSTANCE.getDao().saveList(dlmsDeviceList);
		System.out.print(".");
	}

	private void findCoreDevice() {
		for (int i = 1; i < MAX; i++) {
			String devid = DC.PREFIX_DEVICE + i;
			DeviceMsg deviceMsg = CoreDaoFact.INSTANCE.getDao().getCoreDevice(devid);
			Assert.assertNotNull(deviceMsg);
		}
	}

	private void findDlmsDevice() {
		try {
			for (int i = 1; i < MAX; i++) {
				String devid = DC.PREFIX_DEVICE + i;
				DlmsDeviceMsg deviceMsg;
				deviceMsg = DlmsDaoFact.INSTANCE.getDao().findByDeviceId(devid);
				Assert.assertNotNull(deviceMsg);
			}
		} catch (FunctionalException e) {
			e.printStackTrace();
		}
	}

	private void showProgress(int i) {
		if (i % 50000 == 0)
			System.out.print(".");
	}

	private static int askInsertTotal() {
		System.out.println("Hoeveel devices wil je inserten? : ");
		try {
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			String s = bufferRead.readLine();
			return new Integer(s).intValue();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static DeviceMsg makeDeviceMsg(String devid) {
		DeviceMsg device = DeviceMsg.newBuilder().setDeviceId(devid).setActivated(Boolean.TRUE).setLat(1234567)
				.setLat(56789).setNetworkAddress("localhost").setProtocol("GPRS").setOrganisations(CC.INFOSTROOM).build();
		return device;
	}

}
