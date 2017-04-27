package org.osgp.pa.dlms.dlms.cmdexec;

public enum CosemInterfaceClass {

	REGISTER(3), EXTENDED_REGISTER(4), DEMAND_REGISTER(5), PROFILE_GENERIC(7), CLOCK(8);

	private int id;

	private CosemInterfaceClass(int id) {
		this.id = id;
	}

	public int id() {
		return this.id;
	}
	
}