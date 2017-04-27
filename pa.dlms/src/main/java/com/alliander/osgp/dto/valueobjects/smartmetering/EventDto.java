package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

import org.joda.time.DateTime;

public class EventDto implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 5484936946786037616L;
    private DateTime timestamp;
    private Integer eventCode;
    private Integer eventCounter;

    public EventDto(final DateTime timestamp, final Integer eventCode, final Integer eventCounter) {
        this.timestamp = timestamp;
        this.eventCode = eventCode;
        this.eventCounter = eventCounter;
    }

    public DateTime getTimestamp() {
        return this.timestamp;
    }

    public Integer getEventCode() {
        return this.eventCode;
    }

    public Integer getEventCounter() {
        return this.eventCounter;
    }

}
