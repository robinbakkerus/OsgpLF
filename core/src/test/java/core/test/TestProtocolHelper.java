package core.test;

import org.junit.Assert;
import org.junit.Test;
import org.osgp.core.ProtocolHelper;

public class TestProtocolHelper {

	@Test
	public void test() {
		ProtocolHelper.initialize();
		String s = ProtocolHelper.getStrValue("dlms", "platform-adapter.response-actor");
		Assert.assertNotNull(s);
		System.out.println(s);
	}

}
