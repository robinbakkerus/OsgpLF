package core.test;

import java.util.UUID;

import org.junit.Test;

public class TestPK {

	@Test
	public void test() {
		String s = UUID.randomUUID().toString();
		System.out.println(s);
		System.out.println(s.hashCode());
	}

}
