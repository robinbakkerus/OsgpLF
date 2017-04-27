package org.osgp.client.service;

import org.osgp.client.dao.ClientDaoFact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.DlmsDevOperMsg;
import com.alliander.osgp.dlms.EmptyMsg;
import com.alliander.osgp.dlms.FindEventsMsg;
import com.alliander.osgp.dlms.FindEventsMsg.EventLogCatgory;
import com.alliander.osgp.dlms.GetSpecificObjectMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.shared.CommonMsg;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import io.grpc.StatusRuntimeException;

public class DevOpsGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(DevOpsGenerator.class);

	private Config config = ConfigFactory.load("test-client");

	public void doInsert(int total) {
		this.doInsert("mix", total);
	}
	
	public void doInsert(String operation, int total) {
		try {
			for (int i = 1; i <= total; i++) {
		        insertDeviceOperations(operation, i);
				if (i % 50000 == 0) System.out.print(".");
			}
			System.out.println(n);
		} catch (StatusRuntimeException e) {
			LOGGER.warn("RPC failed: {0}", e.getStatus());
			return;
		}
	}

	private void insertDeviceOperations(String operation, int i) {
		if (operation.equals(RequestType.GET_ACTUAL_METER_READS.name())) {
			insertDevOper(makeGetActualMeterRead(i));
		} else if (operation.equals(RequestType.GET_CONFIGURATION.name())) {
			insertDevOper(makeGetConfiguration(i));
		} else if (operation.equals(RequestType.FINDEVENTS.name())) {
			insertDevOper(makeFindEvents(i));
		} else {
			insertMixOfDevOps(i);
		}
	}


	private void insertMixOfDevOps(int i) {
		DlmsDevOperMsg devoper = null;
		if (Math.random() > 1.0 - config.getDouble("make-action.GetActualMeterReads.value")) {
			devoper = makeGetActualMeterRead(i);
		}

		if (Math.random() > 1.0 - config.getDouble("make-action.GetConfiguration.value")) {
		    devoper = makeGetConfiguration(i);
		}
		
		if (Math.random() > 1.0 - config.getDouble("make-action.FindEvents.value")) {
			devoper = makeFindEvents(i);
		}
		insertDevOper(devoper);
	}


	private DlmsDevOperMsg makeFindEvents(int i) {
		DlmsDevOperMsg devoper3 = DlmsDevOperMsg.newBuilder()
				.setCommon(makeCommon(i))
				.setAction(buildFindEvents()).build();
		return devoper3;
	}


	private DlmsDevOperMsg makeGetConfiguration(int i) {
		DlmsDevOperMsg devoper2 = DlmsDevOperMsg.newBuilder()
				.setCommon(makeCommon(i))
				.setAction(buildGetConfiguration()).build();
		return devoper2;
	}


	private DlmsDevOperMsg makeGetActualMeterRead(int i) {
		DlmsDevOperMsg devoper1 = DlmsDevOperMsg.newBuilder()
				.setCommon(makeCommon(i))
				.setAction(buildGetActualMeterReads()).build();
		return devoper1;
	}
	
	private int n = 0;

	private void insertDevOper(DlmsDevOperMsg devoper) {
		ClientDaoFact.INSTANCE.getDao().saveDeviceOperation(devoper);
		n++;
	}


	private DlmsActionMsg buildGetActualMeterReads() {
		EmptyMsg m = EmptyMsg.newBuilder().build();
		return DlmsActionMsg.newBuilder().setRequestType(RequestType.GET_ACTUAL_METER_READS).setGetActualMeterReadMsg(m).build();
	}
	
	private DlmsActionMsg buildGetConfiguration() {
//		ObisCodeMgs obiscode = ObisCodeMgs.newBuilder().setA(1).setB(1).setC(1).setD(1).setE(1).setF(255).build();
		String obiscode = "1.1.1.1.1.255";
		GetSpecificObjectMsg specobj = GetSpecificObjectMsg.newBuilder().setClassid(1).setAttrribute(1)
				.setObisCode(obiscode).build();
		return DlmsActionMsg.newBuilder().setRequestType(RequestType.GET_SPECIFIC_OBJECT)
				.setGetSpecificObjectMsg(specobj).build();
	}

	private DlmsActionMsg buildFindEvents() {
		FindEventsMsg m = FindEventsMsg.newBuilder().setCategory(EventLogCatgory.FRAUD_DETECTION_LOG).setEventsFrom(1L)
				.setEventsUntil(2L).build();
		return DlmsActionMsg.newBuilder().setRequestType(RequestType.FINDEVENTS).setFindEventsMsg(m).build();
	}
	
	private CommonMsg makeCommon(final int index) {
		return CommonMsg.newBuilder().setApplicationName("Appname")
				.setDeviceId(deviceId(index))
				.setUserName("robinb")
				.build();
	}

	private String deviceId(int index) {
		if (getFaalKans() == 0.0d || getFaalKans() <  Math.random()) {
			return "DEV" + index;
		} else {
			return "BAD" + index;
		}
	}
	
	Double faalKans = null;
	
	private double getFaalKans() {
		if (faalKans == null) {
			if (System.getProperty("fail") != null) {
				faalKans = Double.parseDouble(System.getProperty("fail"));
			} else {
				faalKans = 0.0d;
			}
		}
		return faalKans;
	}
	
}
