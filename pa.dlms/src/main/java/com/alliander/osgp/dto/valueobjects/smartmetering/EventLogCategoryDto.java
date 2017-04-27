package com.alliander.osgp.dto.valueobjects.smartmetering;

public enum EventLogCategoryDto {
    STANDARD_EVENT_LOG(2),
    FRAUD_DETECTION_LOG(2),
    COMMUNICATION_SESSION_LOG(3),
    M_BUS_EVENT_LOG(2);

    private int numberOfEventLogElements;

    private EventLogCategoryDto(final int numberOfEventLogElements) {
        this.numberOfEventLogElements = numberOfEventLogElements;
    }

    public int getNumberOfEventElements() {
        return this.numberOfEventLogElements;
    }
}
