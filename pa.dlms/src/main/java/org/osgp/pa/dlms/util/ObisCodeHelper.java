package org.osgp.pa.dlms.util;

import org.apache.commons.lang3.StringUtils;
import org.openmuc.jdlms.ObisCode;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;

import com.alliander.osgp.dto.valueobjects.smartmetering.ObisCodeValuesDto;

public class ObisCodeHelper {

	public static ObisCode makeObisCode(final  String logicalName) throws ProtocolAdapterException {
		String tags[] = StringUtils.split(logicalName, ".");
		if (tags.length != 6) {
			throw new ProtocolAdapterException("wrong obiscode " + logicalName);
		}
			
		byte bytes[] = new byte[] {b(tags[0]), b(tags[1]), b(tags[2]), b(tags[3]), b(tags[4]), b(tags[5])};
		return new ObisCode(bytes);
	}
	
	private static byte b(final String s) {
		if ("255".equals(s)) return (byte) -1;
		else {
			return Byte.parseByte(s);
		}
	}
	
	public static ObisCodeValuesDto makeObisCodeValuesDto(final String logicalName) throws ProtocolAdapterException {
		String tags[] = StringUtils.split(logicalName, ".");
		if (tags.length != 6) {
			throw new ProtocolAdapterException("wrong obiscode " + logicalName);
		}
			
		return new ObisCodeValuesDto(b(tags[0]), b(tags[1]), b(tags[2]), b(tags[3]), b(tags[4]), b(tags[5]));
	}
}
