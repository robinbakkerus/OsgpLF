package org.osgp.smint.test.dao;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgp.smint.dao.SmIntDao;
import org.osgp.smint.dao.SmIntDaoFact;
import org.osgp.smint.dao.SmIntDbsMgr;
import org.osgp.smint.dao.SmIntTable;
import org.osgp.util.dao.PK;

import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.ResponseMsg;
import com.alliander.osgp.shared.ResponseStatus;

public class TestRequestResponseMsgDao {


	private static final long JOB_ID = 1L;
	

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
	public void testSaveRequestResponseMsg() throws IOException {
		RequestResponseMsg reqRespMsg1 = makeRequestResponseMsg();
		dao().saveRequestResponse(reqRespMsg1, SmIntTable.RR_NEW);
		
		RequestResponseMsg reqRespMsg2 = dao().getRequestResponse(reqRespMsg1.getCorrelId(), SmIntTable.RR_NEW);
		Assert.assertTrue(reqRespMsg2.getCorrelId().equals(reqRespMsg1.getCorrelId()));
		
		List<RequestResponseMsg> reqrespmsgList = dao().getRequestResponsesByJobId(JOB_ID, SmIntTable.RR_NEW);
		Assert.assertTrue(reqrespmsgList.size() > 0);
		
		List<RequestResponseMsg> allmsg = dao().getAllRequestResponses(SmIntTable.RR_NEW);
		List<PK> allPks = allmsg.stream().map(f -> new PK(f.getCorrelId(), SmIntTable.RR_NEW.getTableName())).collect(Collectors.toList());
		dao().deleteList(SmIntTable.RR_NEW, allPks);
		
		List<RequestResponseMsg> allmsg2 = dao().getAllRequestResponses(SmIntTable.RR_NEW);
		Assert.assertTrue(allmsg2.size() == 0);
	}

	private RequestResponseMsg makeRequestResponseMsg() {
		final String correlId = UUID.randomUUID().toString();
		return RequestResponseMsg.newBuilder()
				.setCorrelId(correlId)
				.setCommon(makeCommonMsg())
				.setResponse(ResponseMsg.newBuilder().setStatus(ResponseStatus.NOT_OK).build()).build();
	}

	private CommonMsg makeCommonMsg() {
		return CommonMsg.newBuilder().setJobId(JOB_ID).build();
	}
	
	private SmIntDao dao() {
		return SmIntDaoFact.INSTANCE.getDao();
	}

}
