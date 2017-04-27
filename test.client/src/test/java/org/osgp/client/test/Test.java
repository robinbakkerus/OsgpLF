package org.osgp.client.test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;

public class Test {

	@org.junit.Test
	public void test() {
		DateTime dt = new DateTime();
		dt.isBefore(null);
		
		Date date = new Date();
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		date = cal.getTime();
		
		System.out.println(date);
	}

}
