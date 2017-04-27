package org.osgp.util;

public class RoundRobin {

	private int index = 0;
	private int max = 0;
	
	public RoundRobin(final int max) {
		super();
		this.max = max;
	}

	public int getIndex() {
		return index;
	}
	
	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int nextIndex() {
		if (this.index < max-1) {
			this.index++;
		} else {
			this.index = 0;
		}
		return this.index;
	}
	
}
