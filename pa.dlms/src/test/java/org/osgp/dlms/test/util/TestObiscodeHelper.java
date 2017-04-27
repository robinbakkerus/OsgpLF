package org.osgp.dlms.test.util;

import org.junit.Test;
import org.openmuc.jdlms.ObisCode;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.pa.dlms.util.ObisCodeHelper;

import com.alliander.osgp.dto.valueobjects.smartmetering.ObisCodeValuesDto;

public class TestObiscodeHelper {

	@Test
	public void test1() throws ProtocolAdapterException {
		ObisCode obiscode = ObisCodeHelper.makeObisCode("10.0.99.1.0.255");
		System.out.println(obiscode);
	}

	@Test
	public void test2() throws ProtocolAdapterException {
		ObisCodeValuesDto obiscode = ObisCodeHelper.makeObisCodeValuesDto("10.0.99.1.0.255");
		System.out.println(obiscode);
	}
}
