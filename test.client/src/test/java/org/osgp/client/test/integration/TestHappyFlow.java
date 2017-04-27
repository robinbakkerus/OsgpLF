package org.osgp.client.test.integration;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import org.junit.Assert;
import org.junit.Test;

import com.alliander.osgp.dlms.DlmsSpecificMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.shared.RequestResponseMsg;
import com.alliander.osgp.shared.ResponseStatus;

public class TestHappyFlow extends AbstractTestBase {

//	private static final Logger LOGGER = LoggerFactory.getLogger(TestHappyFlow.class);

	@Test
	public void testNominalFlow() {
		try {
			System.err.println("start");
			long startTime = System.nanoTime();
			nominalFlow(RequestType.GET_ACTUAL_METER_READS);
			long seconds = NANOSECONDS.toSeconds(System.nanoTime() - startTime);
			System.out.println("" + nrOfDevOpsToInsert + " devops finished in " + seconds + " seconds");
		} catch (Exception e) {
			Assert.fail("error " + e);
		}
	}

	@Override
	protected void checkResponse(final RequestResponseMsg reqRespMsg, final DlmsSpecificMsg specificMsg) {
		Assert.assertEquals("status should be OK",  ResponseStatus.OK,reqRespMsg.getResponse().getStatus());
	}
	
	@Override
	protected void setParams() {
		setParam("make-action.GetActualMeterReads.value", "1.0");
	}
}
