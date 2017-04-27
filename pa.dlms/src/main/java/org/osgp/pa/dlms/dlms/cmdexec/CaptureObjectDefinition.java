package org.osgp.pa.dlms.dlms.cmdexec;

public enum CaptureObjectDefinition {

	CLASS_ID(0), LOGICAL_NAME(1), ATTRIBUTE_INDEX(2), DATA_INDEX(3);

	private int index;

	private CaptureObjectDefinition(int index) {
		this.index = index;
	}

	public int index() {
		return this.index;
	}
}

