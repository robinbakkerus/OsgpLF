package org.osgp.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.FlowMsg;
import com.alliander.osgp.shared.FlowPhase;
import com.alliander.osgp.shared.FlowPhaseMsg;
import com.alliander.osgp.shared.PropMsg;
import com.alliander.osgp.shared.PropMsgType;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.ResponseMsg;
import com.alliander.osgp.shared.ResponseStatus;
import com.alliander.osgp.shared.ResponseValuesMsg;
import com.alliander.osgp.shared.ResponseValuesType;

public class MsgMapper {

	private MsgMapper() {}
	
	/**
	 * This generates a ResponseMsg
	 * @param status
	 * @param code
	 * @param key
	 * @param value
	 * @return
	 */
	public static ResponseMsg simpleResponseMsg(final ResponseStatus status,
			final String code, final String key, final String value) {
		
		PropMsg propMsg = PropMsg.newBuilder().setKey(key).setValue(value).build();
		ResponseValuesMsg responseValues = ResponseValuesMsg.newBuilder().setStatus(status).addProperties(propMsg).build();
		
		return ResponseMsg.newBuilder()
				.setStatus(status)
				.addValues(responseValues)
				.build();
	}
	
	
	public static RequestResponseMsg makeSendReqRespToCore(final RequestResponseMsg reqRespMsg, final ResponseMsg response,
			final boolean retry) {
		CommonMsg common = CommonMsg.newBuilder(reqRespMsg.getCommon())
				.setRetry(retry)
				.build();
		
		return RequestResponseMsg.newBuilder(reqRespMsg)
				.setCommon(common)
				.setResponse(response).build();
		
	}	
	
	public static String formatResponse(final ResponseMsg msg) {
		final StringBuffer sb = new StringBuffer();
		sb.append("status = " + msg.getStatus() + "\n");
		return sb.toString();
	}

	public static PropMsg prop(final String key) {
		return PropMsg.newBuilder().setKey(key).setType(PropMsgType.STRING).build();
	}
	
	public static PropMsg prop(final String key, final String value) {
		return PropMsg.newBuilder().setKey(key).setValue(value).setType(PropMsgType.STRING).build();
	}

	public static PropMsg prop(final String key, final int value) {
		return PropMsg.newBuilder().setKey(key).setValue(Integer.valueOf(value).toString()).setType(PropMsgType.INT).build();
	}
	
	public static PropMsg prop(final String key, final double value) {
		return PropMsg.newBuilder().setKey(key).setValue(Double.valueOf(value).toString()).setType(PropMsgType.REAL).build();
	}

	public static PropMsg prop(final String key, final BigDecimal value) {
		return PropMsg.newBuilder().setKey(key).setValue(Double.valueOf(value.doubleValue()).toString()).setType(PropMsgType.REAL).build();
	}

	public static PropMsg prop(final String key, final Date value) {
		return PropMsg.newBuilder().setKey(key).setValue(Long.valueOf(value.getTime()).toString()).setType(PropMsgType.DATE).build();
	}
	
	public static PropMsg prop(final String key, final boolean value) {
		final String strval = value ? "T" : "F";
		return PropMsg.newBuilder().setKey(key).setValue(strval).setType(PropMsgType.BOOL).build();
	}

	public static FlowMsg makeFlow(final FlowMsg flow, final FlowPhase phase) {
		return FlowMsg.newBuilder(flow)
				.addFlowPhases(FlowPhaseMsg.newBuilder().setDate(now()).setPhase(phase))
				.build();
	}
	
	public static ResponseValuesMsg makeResponseValues(final List<PropMsg> properties, final ResponseStatus status, final String action) {
		return ResponseValuesMsg.newBuilder()
				.setResponseValuesType(ResponseValuesType.GENERIC_RESPONSE_VALUES)
				.setStatus(status)
				//.setCode(code)
				.setAction(action)
				.addAllProperties(properties)
				.build();
	}

	public static long now() {
		return new Date().getTime();
	}
}
