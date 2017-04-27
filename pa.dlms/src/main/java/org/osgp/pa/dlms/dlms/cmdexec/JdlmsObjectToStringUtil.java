package org.osgp.pa.dlms.dlms.cmdexec;

import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.MethodParameter;

public class JdlmsObjectToStringUtil {
	   private JdlmsObjectToStringUtil() {
	        // Utility class.
	    }

	    public static String describeAttributes(final AttributeAddress... attributeAddresses) {

	        if (attributeAddresses == null || attributeAddresses.length == 0) {
	            return "";
	        }

	        final StringBuilder sb = new StringBuilder();
	        for (final AttributeAddress attributeAddress : attributeAddresses) {
	            sb.append(", {").append(attributeAddress.getClassId()).append(',').append(attributeAddress.getInstanceId())
	            .append(',').append(attributeAddress.getId()).append('}');
	        }
	        return sb.substring(2);
	    }

	    public static String describeMethod(final MethodParameter methodParameter) {

	        if (methodParameter == null) {
	            return "";
	        }

	        final StringBuilder sb = new StringBuilder();
	        sb.append('{').append(methodParameter.getClassId()).append(',').append(methodParameter.getInstanceId())
	                .append(',').append(methodParameter.getId()).append('}');
	        return sb.toString();
	    }
	}
