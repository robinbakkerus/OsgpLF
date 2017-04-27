package org.osgp.client.test;

import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgp.client.dao.ClientDbsMgr;
import org.osgp.platform.dbs.PlatformDao;
import org.osgp.platform.dbs.PlatformDaoFact;
import org.osgp.platform.dbs.PlatformDbsMgr;
import org.osgp.shared.CC;

import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.ResponseMsg;
import com.alliander.osgp.shared.ResponseStatus;
import com.google.protobuf.InvalidProtocolBufferException;

public class TestUpdateRequestResponse implements CC {

//    private static final Logger LOGGER = LoggerFactory.getLogger(TestUpdateRequestResponse.class.getName());

	@BeforeClass
	public static void beforeOnce() {
		PlatformDbsMgr.INSTANCE.open();
	}

	@AfterClass
	public static void afterOnce() {
		PlatformDbsMgr.INSTANCE.close();
	}

    @Test
    public void test() throws InvalidProtocolBufferException {
    	final String correlId = UUID.randomUUID().toString();
        RequestResponseMsg reqRespMsg = RequestResponseMsg.newBuilder()
        		.setCorrelId(correlId)
        		.setResponse(ResponseMsg.newBuilder().setStatus(ResponseStatus.NOT_OK).build())
        		.build();
        Assert.assertTrue(reqRespMsg.getCommon().getRetryCount()==0);
        platformDao().saveRequestResponse(reqRespMsg);
        RequestResponseMsg reqRespMsg1 = platformDao().getResponse(correlId);
        Assert.assertTrue(reqRespMsg1.getCommon().getRetryCount()==0);
        
        RequestResponseMsg reqRespMsg2 = incRetryCount(reqRespMsg1);
        Assert.assertTrue(reqRespMsg2.getCommon().getRetryCount()==1);
        platformDao().saveRequestResponse(reqRespMsg2);
        
        RequestResponseMsg reqRespMsg3 = platformDao().getResponse(correlId);
		Assert.assertTrue(reqRespMsg3.getCommon().getRetryCount()==1);
    }

	private RequestResponseMsg incRetryCount(RequestResponseMsg msg) {
		int retrycnt = msg.getCommon().getRetryCount();
		CommonMsg common = CommonMsg.newBuilder(msg.getCommon()).setRetryCount(retrycnt+1).build();
		
		RequestResponseMsg result = RequestResponseMsg.newBuilder(msg)
				.setCommon(common)
				.setDevice(msg.getDevice())
				.setResponse(msg.getResponse())
				.build();
		return result;
	}
	
	
	private PlatformDao platformDao() {
		return PlatformDaoFact.INSTANCE.getDao();
	}

}

