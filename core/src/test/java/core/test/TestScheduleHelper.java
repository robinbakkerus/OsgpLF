package core.test;

import java.util.Date;

import org.junit.Test;
import org.osgp.core.utils.RescheduleHelper;

import com.alliander.osgp.shared.CommonMsg;
import com.alliander.osgp.shared.RequestResponseMsg;

public class TestScheduleHelper {


	@Test
	public void test() {
		CommonMsg common = CommonMsg.newBuilder().setRetryCount(1).build();
		
		long nu = System.currentTimeMillis();
		
		RequestResponseMsg msg = RequestResponseMsg.newBuilder().setCommon(common).build();
		long schedtime = RescheduleHelper.calcScheduleTime(msg);
		
		System.out.println(nu + " = " + new Date(nu));
		System.out.println(schedtime + " = " + new Date(schedtime));
	}

}
