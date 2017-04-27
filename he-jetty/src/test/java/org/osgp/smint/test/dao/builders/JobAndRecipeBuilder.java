package org.osgp.smint.test.dao.builders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.osgp.util.MsgMapper;

import com.alliander.osgp.dlms.DeviceGroupMsg;
import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.EmptyMsg;
import com.alliander.osgp.dlms.FindEventsMsg;
import com.alliander.osgp.dlms.FindEventsMsg.EventLogCatgory;
import com.alliander.osgp.dlms.JobMsg;
import com.alliander.osgp.dlms.RecipeMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.PropMsg;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.ResponseMsg;
import com.alliander.osgp.shared.ResponseStatus;
import com.alliander.osgp.shared.ResponseValuesMsg;


public class JobAndRecipeBuilder {

	public static final long JOB_ID_1 = 1L;

	public static JobMsg makeJob(final long id) {
		return JobMsg.newBuilder()
				.setId(id)
				.setName("Job-" + id)
				.setDeviceGroupId(1L)
				.setRecipeId(1L)
				.setCreationTime(new Date().getTime())
				.setDevopsTotal(100)
				.setDevopsSuccess(98)
				.setDevopsFailed(2)
				.setStatus(ResponseStatus.OK)
				.build();
	}

//	public static HeDevOpSummMsg makeDevOp() {
//		return HeDevOpSummMsg.newBuilder()
//				.setDeviceId("DEV1")
//				.setCorrelId("correlid-id-1")
//				.setResponseSummary(makeResponseSumm())
//				.build();
//	}
	
	
	private static DlmsActionMsg action1() {
		return DlmsActionMsg.newBuilder()
				.setRequestType(RequestType.GET_CONFIGURATION)
				.setGetConfigurationMsg(emptyMsg())
				.build();
	}
	
	private static DlmsActionMsg action2() {
		return DlmsActionMsg.newBuilder()
				.setRequestType(RequestType.FINDEVENTS)
				.setFindEventsMsg(findEventsMsg())
				.build();
	}

	private static EmptyMsg emptyMsg() {
		return EmptyMsg.newBuilder().build();
	}
	
	private static String makeResponseSumm() {
		StringBuilder sb = new StringBuilder();
		sb.append("action : GetConfiguration<br>");
		sb.append("label1 : waarde1<br>");
		sb.append("label2 : waarde2<br>");
		sb.append("label3 : waarde3<br>");
		return sb.toString();
	}
	
	private static FindEventsMsg findEventsMsg() {
		return FindEventsMsg.newBuilder().setCategory(EventLogCatgory.M_BUS_EVENT_LOG)
				.setEventsFrom(new Date().getTime())
				.build();
	}
	
	public static RecipeMsg makeRecipe(int n) {
		return RecipeMsg.newBuilder()
				.setId(n)
				.setName("Recipe " + n)
				.addActions(action1())
				.addActions(action2())
				.build();
	}
	
	private static long now() {
		return new Date().getTime();
	}
	
	public static RequestResponseMsg makeRequestResponseMsg(final long jobId) {
		final String correlId = UUID.randomUUID().toString();
		return RequestResponseMsg.newBuilder()
				.setCorrelId(correlId)
				.setCommon(makeCommonMsg(jobId))
				.setResponse(makeResponseMsg()).build();
	}

	private static CommonMsg makeCommonMsg(final long jobId) {
		return CommonMsg.newBuilder().setJobId(jobId).setDeviceId("DEV" + jobId).build();
	}

	public static DeviceGroupMsg makeSingleDeviceGroupMsg() {
		return DeviceGroupMsg.newBuilder()
				.setId(1L)
				.setName("Single Device group")
				.setDescription("Group with 1 device")
				.setCreationTime(now())
				.addDeviceIds("DEV1")
				.build();
	}

	public static  DeviceGroupMsg make1KDeviceGroupMsg() {
		return DeviceGroupMsg.newBuilder()
				.setId(1000L)
				.setName("1K Devices group")
				.setDescription("Group with 1K devices")
				.setCreationTime(now())
				.addAllDeviceIds(make1KDeviceIds(1000))
				.build();
	}
	
	public static  DeviceGroupMsg make10KDeviceGroupMsg() {
		return DeviceGroupMsg.newBuilder()
				.setId(10000L)
				.setName("10K Devices group")
				.setDescription("Group with 10K devices")
				.setCreationTime(now())
				.addAllDeviceIds(make1KDeviceIds(10000))
				.build();
	}
	
	public static  DeviceGroupMsg make50KDeviceGroupMsg() {
		return DeviceGroupMsg.newBuilder()
				.setId(50000L)
				.setName("50K Devices group")
				.setDescription("Group with 50K devices")
				.setCreationTime(now())
				.addAllDeviceIds(make1KDeviceIds(50000))
				.build();
	}
	
	public static  DeviceGroupMsg make100KDeviceGroupMsg() {
		return DeviceGroupMsg.newBuilder()
				.setId(100000L)
				.setName("100K Devices group")
				.setDescription("Group with 100K devices")
				.setCreationTime(now())
				.addAllDeviceIds(make1KDeviceIds(100000))
				.build();
	}

	public static  DeviceGroupMsg make1MDeviceGroupMsg() {
		return DeviceGroupMsg.newBuilder()
				.setId(100000L)
				.setName("1M Devices group")
				.setDescription("Group with 1M devices")
				.setCreationTime(now())
				.addAllDeviceIds(make1KDeviceIds(1000000))
				.build();
	}


	private static  List<String> make1KDeviceIds(final int n) {
		List<String> r = new ArrayList<>();
		for (int i=1; i<=n; i++) {
			r.add("DEV" + i);
		}
		return r;
	}
	
	private static ResponseMsg makeResponseMsg() {
		return ResponseMsg.newBuilder().setAction("SpecificAction")
				.setStatus(ResponseStatus.OK)
				.addAllValues(makeProperties())
				.build();
	}
	
	private static List<ResponseValuesMsg> makeProperties() {
		List<ResponseValuesMsg> r = new ArrayList<>();
		r.add(ResponseValuesMsg.newBuilder().setAction("FindEvents").addAllProperties(makeProps()).build());
		return r;
	}
	
	private static List<PropMsg> makeProps() {
		List<PropMsg> r = new ArrayList<>();
		r.add(MsgMapper.prop("key1", "value-1"));
		r.add(MsgMapper.prop("ke2", new Date()));
		r.add(MsgMapper.prop("ke3", 1235));
		return r;
	}
	

}
