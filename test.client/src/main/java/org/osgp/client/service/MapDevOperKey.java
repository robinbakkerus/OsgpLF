package org.osgp.client.service;

public class MapDevOperKey {

	private String deviceId;
	private long scheduleFor;
	private DevOperType devOperType;
	
	public MapDevOperKey(String deviceId, long scheduleFor, DevOperType devOperType) {
		super();
		this.deviceId = deviceId;
		this.scheduleFor = scheduleFor;
		this.devOperType = devOperType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public long getScheduleFor() {
		return scheduleFor;
	}

	public DevOperType getDevOperType() {
		return devOperType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((devOperType == null) ? 0 : devOperType.hashCode());
		result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		MapDevOperKey other = (MapDevOperKey) obj;
//		if (!devOperType.equals(other.devOperType)) return false;
		if (!deviceId.equals(other.deviceId)) return false;
		return isEqualScheduleFor(scheduleFor, other.scheduleFor);
	}
	
	private boolean isEqualScheduleFor(final long date1, final long date2) {
		if (date1 == 0L && date2 == 0L) {
			return true;
		} else if (date1 == 0L || date2 == 0L) {
			return false;
		} else {
			return Math.abs(date1 - date2) < 60000; //TODO configurable
		}
	}
	
}
