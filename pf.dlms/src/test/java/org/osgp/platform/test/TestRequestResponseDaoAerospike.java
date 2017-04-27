package org.osgp.platform.test;

import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgp.platform.dbs.PlatformDao;
import org.osgp.platform.dbs.PlatformDaoAerospikeImpl;
import org.osgp.shared.dbs.AerospikeServer;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.DlmsSpecificMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.shared.ActionMsg;
import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.CorrelIdMsg;
import com.alliander.osgp.shared.ProtocolSpecificMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.google.protobuf.InvalidProtocolBufferException;


public class TestRequestResponseDaoAerospike {

	private static AerospikeServer sAerospikeServer;

	@BeforeClass
	public static void beforeOnce() {
		sAerospikeServer = new AerospikeServer("localhost", 3000);
	}

	@Test
	public void test() throws InvalidProtocolBufferException {
		PlatformDao dao = new PlatformDaoAerospikeImpl();
		final String correlid = UUID.randomUUID().toString();
		RequestResponseMsg reqRespMsg1 = makeRequestMsg(correlid);
		dao.saveRequestResponse(reqRespMsg1);
		RequestResponseMsg reqRespMsg2 = RequestResponseMsg.newBuilder(reqRespMsg1).build();
		dao.saveRequestResponse(reqRespMsg2);
		RequestResponseMsg response = dao.getResponse(makeGetResponseCorrelIdMsg(correlid));
		Assert.assertNotNull(response);
	}

	@AfterClass
	public static void cleanup() {
		sAerospikeServer.cleanup();
	}

	private RequestResponseMsg makeRequestMsg(final String correlid) {
		return RequestResponseMsg.newBuilder()
				.setCommon(makeCommon())
				.setCorrelId(correlid)
				.setAction(makeAction())
				.build();
	}

	private CommonMsg makeCommon() {
		CommonMsg common = CommonMsg.newBuilder().setApplicationName("Appname").setDeviceId("EX12345")
				.setUserName("robinb").build();
		return common;
	}
	
	private ActionMsg makeAction() {
		return ActionMsg.newBuilder()
				.setProtocolSpecific(makeSpecific())
				.build();
	}
	
	private ProtocolSpecificMsg makeSpecific() {
		DlmsActionMsg msgitem1 = DlmsActionMsg.newBuilder().setRequestType(RequestType.FINDEVENTS).build();
		DlmsActionMsg msgitem2 = DlmsActionMsg.newBuilder().setRequestType(RequestType.GET_CONFIGURATION).build();
		DlmsActionMsg msgitem3 = DlmsActionMsg.newBuilder().setRequestType(RequestType.GET_SPECIFIC_OBJECT).build();

		DlmsSpecificMsg specific = DlmsSpecificMsg.newBuilder()
				.addActions(msgitem1)
				.addActions(msgitem2)
				.addActions( msgitem3).build();

		ProtocolSpecificMsg r = ProtocolSpecificMsg.newBuilder()
				.setRaw(specific.toByteString())
				.build();
		
		return r;
	}
	
	
	private CorrelIdMsg makeGetResponseCorrelIdMsg(final String correlid) {
		return CorrelIdMsg.newBuilder().setCorrelid(correlid).build();
	}
			

}
