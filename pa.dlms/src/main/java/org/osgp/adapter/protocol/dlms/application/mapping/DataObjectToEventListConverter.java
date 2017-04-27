package org.osgp.adapter.protocol.dlms.application.mapping;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dto.valueobjects.smartmetering.EventDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventLogCategoryDto;

public class DataObjectToEventListConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataObjectToEventListConverter.class);

    private DlmsHelperService dlmsHelperService;

    public List<EventDto> convert(final DataObject source, final EventLogCategoryDto eventLogCategory)
            throws ProtocolAdapterException {
        final List<EventDto> eventList = new ArrayList<>();
        if (source == null) {
            throw new ProtocolAdapterException("DataObject should not be null");
        }

        final List<DataObject> listOfEvents = source.getValue();
        for (final DataObject eventDataObject : listOfEvents) {
            eventList.add(this.getEvent(eventDataObject, eventLogCategory));
        }

        return eventList;

    }

    private EventDto getEvent(final DataObject eventDataObject, final EventLogCategoryDto eventLogCategory)
            throws ProtocolAdapterException {

        final List<DataObject> eventData = eventDataObject.getValue();

        if (eventData == null) {
            throw new ProtocolAdapterException("eventData DataObject should not be null");
        }

        if (eventData.size() != eventLogCategory.getNumberOfEventElements()) {
            throw new ProtocolAdapterException("eventData size should be "
                    + eventLogCategory.getNumberOfEventElements());
        }

        // extract values from List<DataObject> eventData.
        final DateTime dateTime = this.extractDateTime(eventData);
        final Short code = this.extractCode(eventData);
        final Integer eventCounter = this.extractEventCounter(eventLogCategory, eventData);

        LOGGER.info("Event time is {}, event code is {} and event counter is {}", dateTime, code, eventCounter);

        // build a new EventDto with those values.
        return new EventDto(dateTime, code.intValue(), eventCounter);
    }

    private DateTime extractDateTime(final List<DataObject> eventData) throws ProtocolAdapterException {

        final DateTime dateTime = this.dlmsHelperService.convertDataObjectToDateTime(eventData.get(0)).asDateTime();
        if (dateTime == null) {
            throw new ProtocolAdapterException("eventData time is null/unspecified");
        }
        return dateTime;
    }

    private Short extractCode(final List<DataObject> eventData) throws ProtocolAdapterException {

        if (!eventData.get(1).isNumber()) {
            throw new ProtocolAdapterException("eventData value is not a number");
        }
        return eventData.get(1).getValue();
    }

    private Integer extractEventCounter(final EventLogCategoryDto eventLogCategory, final List<DataObject> eventData)
            throws ProtocolAdapterException {

        Integer eventCounter = null;

        if (eventLogCategory.getNumberOfEventElements() == 3) {
            if (!eventData.get(2).isNumber()) {
                throw new ProtocolAdapterException("eventData value is not a number");
            }
            eventCounter = eventData.get(2).getValue();
        }

        return eventCounter;
    }
}
