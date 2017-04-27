package org.osgp.client.test.integration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.osgp.client.dao.ClientDao;
import org.osgp.client.dao.ClientDaoFact;
import org.osgp.client.dao.ClientDbsMgr;
import org.osgp.client.dao.DeviceOperationTuple;
import org.osgp.client.service.DevOpsBundler;
import org.osgp.client.service.DevOpsGenerator;
import org.osgp.client.service.DevOpsSender;
import org.osgp.client.setup.OsgpStarter;
import org.osgp.core.dbs.CoreDao;
import org.osgp.core.dbs.CoreDaoFact;
import org.osgp.core.dbs.CoreDbsMgr;
import org.osgp.dlms.DC;
import org.osgp.platform.dbs.PlatformDao;
import org.osgp.platform.dbs.PlatformDaoFact;
import org.osgp.platform.dbs.PlatformDbsMgr;
import org.osgp.platform.dbs.PlatformTable;
import org.osgp.shared.dbs.perst.PerstUtils;
import org.osgp.util.SystemPropertyHelper;
import org.osgp.util.dao.PK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aerospike.client.Record;
import com.alliander.osgp.dlms.DlmsSpecificMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;

public abstract class AbstractTestBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTestBase.class);

	private ActorSystem system = ActorSystem.create("TEST_CLIENT", ConfigFactory.load(("test-client")));

	private DevOpsGenerator devopsGenerator = new DevOpsGenerator();
	private DevOpsBundler devopsBundler = new DevOpsBundler();
	private DevOpsSender devopsSender = new DevOpsSender(system);

	protected int nrOfDevOpsToInsert = 2500;
	protected long initWait = 2000L;
	protected long loopWait = 1000L;
	protected long maxLoops = 100;

	protected abstract void checkResponse(final RequestResponseMsg reqRespMsg, final DlmsSpecificMsg specificMsg);

	protected abstract void setParams();

	@BeforeClass
	public static void beforeOnce() {
		SystemPropertyHelper.setProperties("stub=true;wait=10");
		ClientDbsMgr.INSTANCE.open();
		PlatformDbsMgr.INSTANCE.open();
		CoreDbsMgr.INSTANCE.open();
	}

	@AfterClass
	public static void afterOnce() {
		ClientDbsMgr.INSTANCE.close();
		SystemPropertyHelper.clearProperties();
	}
	
	@Before
	public void beforeEach() {
		LOGGER.info("cleaning up database...");
		List<PK> delReqResp = platformDao().getAllRequestResponseMsgPKs();
		for (PK pk : delReqResp) {
			platformDao().delete(PlatformTable.REQ_RESP, pk);
		}

		List<PK> delKeys = coreDao().getAllScheduledTaskPks();
		for (PK pk : delKeys) {
			coreDao().delete(pk);
		}

		List<DeviceOperationTuple> tuples = dao().getAllDeviceOperations();
		for (DeviceOperationTuple tuple : tuples) {
			dao().delete(tuple.getPk());
		}

		Assert.assertTrue(getAllSendReqRespMsgs().size() == 0);
		Assert.assertTrue(coreDao().getAllScheduledTaskPks().size() == 0);
		Assert.assertTrue(dao().getAllDeviceOperations().size() == 0);

		LOGGER.info("finished cleaning database.");
	}

	protected void nominalFlow(final RequestType reqType) throws IOException, InterruptedException {
		startOsgp();
		initSysParams();
		setParams();
		Assert.assertTrue("verify that there are no responses when we start", getAllSendReqRespMsgs().isEmpty());
		Assert.assertTrue("verify that there are no responses when we start",
				platformDao().getAllRequestResponseMsgPKs().isEmpty());
		makeBundleAndSendDevOps(reqType);
		List<RequestResponseMsg> allReqRespMsgs = waitForResponses();
		Assert.assertNotNull("soon responses should be available", allReqRespMsgs);
		checkResponses(allReqRespMsgs);
	}

	protected void initSysParams() {
		System.setProperty("make-action.GetActualMeterReads.value", "0.0");
		System.setProperty("make-action.GetConfiguration.value", "0.0");
		System.setProperty("make-action.FindEvents.value", "0.0");
	}

	protected void setParam(final String setParamKey, final String setParamValue) {
		System.setProperty(setParamKey, setParamValue);
	}

	protected void startOsgp() throws IOException, InterruptedException {
		OsgpStarter starter = new OsgpStarter();
		starter.startAll();
		Thread.sleep(5000);
		LOGGER.info("started Platform, Core, Dlms and AuditTrail");
	}

	private String operationName(final RequestType reqType) {
		if (reqType == null) {
			return "mix";
		} else {
			return reqType.name();
		}
	}

	protected void makeBundleAndSendDevOps(final RequestType reqType) {
//		InsertDevOpsAndBundle bundler = new InsertDevOpsAndBundle();
		LOGGER.info("inserting deviceoperations ...");
		devopsGenerator.doInsert(operationName(reqType), nrOfDevOpsToInsert);
		LOGGER.info("start bundling...");
		devopsBundler.execute();
		LOGGER.info("finished bundling, start sending ..");
		devopsSender.send();
		LOGGER.info("finished sending.");
	}

	protected List<RequestResponseMsg> waitForResponses() throws InvalidProtocolBufferException {
		List<RequestResponseMsg> result = new ArrayList<>();
		int cnt = 0;
		try {
			Thread.sleep(initWait);
			while (cnt < maxLoops) {
				Thread.sleep(loopWait);
				for (PK pk : platformDao().getAllRequestResponseMsgPKs()) {
					String correlid = PerstUtils.correlIdFromKey(pk);
					RequestResponseMsg reqRespMsg = parse(platformDao().getResponse(correlid));
					if (reqRespMsg != null && !reqRespMsg.getResponse().toString().isEmpty()) {
						result.add(reqRespMsg);
					}
				}

				if (!result.isEmpty()) {
					return result;
				} else {
					cnt++;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void checkResponses(List<RequestResponseMsg> allReqRespMsgs) throws InvalidProtocolBufferException {
		for (RequestResponseMsg reqRespMsg : allReqRespMsgs) {
			DlmsSpecificMsg specific = DlmsSpecificMsg
					.parseFrom((byte[]) reqRespMsg.getAction().getProtocolSpecific().getRaw().toByteArray());
			checkResponse(reqRespMsg, specific);
		}

	}

	protected List<RequestResponseMsg> getAllSendReqRespMsgs() {
		List<RequestResponseMsg> result = new ArrayList<>();
		List<RequestResponseMsg> allreqs = dao().getAllBundledDeviceOperations();
		for (RequestResponseMsg req : allreqs) {
			RequestResponseMsg reqRespMsg;
			try {
				reqRespMsg = platformDao().getResponse(req.getCorrelId());
				if (reqRespMsg != null && reqRespMsg.getResponse() != null) {
					result.add(reqRespMsg);
				}
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private RequestResponseMsg parse(Object obj) {
		if (obj instanceof RequestResponseMsg) {
			return (RequestResponseMsg) obj;
		} else if (obj instanceof Record) {
			return parseRecord((Record) obj);
		} else if (obj instanceof byte[]) {
			return parseBytes((byte[]) obj);
		} else {
			return null;
		}
		
	}
	
	private RequestResponseMsg parseRecord(Record rec) {
		if (rec != null && rec.getValue(DC.REQ_RESP_MSG) != null) {
			try {
				return RequestResponseMsg.parseFrom((byte[]) rec.getValue(DC.REQ_RESP_MSG));
			} catch (InvalidProtocolBufferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	private RequestResponseMsg parseBytes(byte[] bytes) {
			try {
				return RequestResponseMsg.parseFrom((byte[]) bytes);
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
				return null;
			}
	}

	protected ClientDao dao() {
		return ClientDaoFact.INSTANCE.getDao();
	}

	protected PlatformDao platformDao() {
		return PlatformDaoFact.INSTANCE.getDao();
	}
	
	protected CoreDao coreDao() {
		return CoreDaoFact.INSTANCE.getDao();
	}

}
