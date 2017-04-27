package org.osgp.shared.dlms.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.osgp.dlms.MsgUtils;
import org.osgp.shared.exceptionhandling.TechnicalException;
import org.osgp.util.MsgMapper;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.DlmsSpecificMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.shared.ActionMsg;
import com.alliander.osgp.shared.ProtocolSpecificMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.ResponseMsg;
import com.alliander.osgp.shared.ResponseValuesMsg;

public class TestMsgUtils {

	@Test
	public void test1() {
		RequestResponseMsg reqRespMsg = makeEmptyRequestResponseMsg();
		Assert.assertFalse(MsgUtils.hasProtocolSpecificAction(reqRespMsg));
	}
	
	@Test
	public void test2() {
		RequestResponseMsg reqRespMsg = makeResponseMsgWithMultipleActions();
		Assert.assertTrue(MsgUtils.hasProtocolSpecificAction(reqRespMsg));
	}
	
	@Test
	public void test3() throws TechnicalException {
		RequestResponseMsg reqRespMsg = makeResponseMsgWithMultipleActions();
		List<ResponseValuesMsg> respValues = MsgUtils.getResponseValues(reqRespMsg);
		Assert.assertTrue(respValues.size() > 0);
		respValues.forEach(f -> printRespValues(f));
	}	
	
	private void printRespValues(final ResponseValuesMsg respValue) {
		 System.out.println(respValue.getAction());
		 
	}
	
	private RequestResponseMsg makeEmptyRequestResponseMsg() {
		return RequestResponseMsg.newBuilder().build();
	}

	private RequestResponseMsg makeResponseMsgWithMultipleActions() {
		return RequestResponseMsg.newBuilder()
				.setAction(ActionMsg.newBuilder().setProtocolSpecific(makeSpecific()))
				.setResponse(makeResponse("overall"))
				.build();
	}

	private ResponseMsg makeResponse(final String action) {
		ResponseValuesMsg respValue = ResponseValuesMsg.newBuilder().setAction(action)
		.addProperties(MsgMapper.prop("label-1", "string waarde"))
		.build();		

		return ResponseMsg.newBuilder().addValues(respValue).build();
	}

	private ResponseValuesMsg respValue(final String action) {
		return ResponseValuesMsg.newBuilder().setAction(action)
		.addProperties(MsgMapper.prop("label-1", "string waarde"))
		.build();		
	}

	private ProtocolSpecificMsg makeSpecific() {
		
		DlmsActionMsg msgitem1 = DlmsActionMsg.newBuilder().setResponse(respValue("FindEvents"))
				.setRequestType(RequestType.FINDEVENTS).build();
		DlmsActionMsg msgitem2 = DlmsActionMsg.newBuilder().setResponse(respValue("getConfiguration"))
				.setRequestType(RequestType.GET_CONFIGURATION).build();
		DlmsActionMsg msgitem3 = DlmsActionMsg.newBuilder().setResponse(respValue("getSpecificObject"))
				.setRequestType(RequestType.GET_SPECIFIC_OBJECT).build();

		DlmsSpecificMsg specific = DlmsSpecificMsg.newBuilder()
				.addActions(msgitem1)
				.addActions(msgitem2)
				.addActions( msgitem3).build();

		ProtocolSpecificMsg r = ProtocolSpecificMsg.newBuilder()
				.setRaw(specific.toByteString())
				.build();
		
		return r;
	}
		
}
