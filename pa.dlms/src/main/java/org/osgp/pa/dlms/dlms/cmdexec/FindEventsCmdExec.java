package org.osgp.pa.dlms.dlms.cmdexec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SelectiveAccessDescription;
import org.openmuc.jdlms.datatypes.DataObject;
import org.osgp.adapter.protocol.dlms.application.mapping.DataObjectToEventListConverter;
import org.osgp.adapter.protocol.dlms.domain.commands.DlmsHelperService;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.pa.dlms.exceptions.ConnectionException;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.util.MsgMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.dlms.DlmsActionMsg;
import com.alliander.osgp.dlms.FindEventsMsg;
import com.alliander.osgp.dlms.RequestType;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventLogCategoryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsRequestDto;
import com.alliander.osgp.shared.PropMsg;
import com.alliander.osgp.shared.ResponseStatus;
import com.alliander.osgp.shared.ResponseValuesMsg;
import static org.osgp.util.MsgMapper.prop;

@AnnotCommandExecutor(action = RequestType.FINDEVENTS)
public class FindEventsCmdExec extends AbstractCommandExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(FindEventsCmdExec.class);
    
	@Override
	public ResponseValuesMsg execute(DlmsConnection conn, DlmsDevice device, DlmsActionMsg reqItem)
			throws ProtocolAdapterException {
		
		final FindEventsMsg msg = reqItem.getFindEventsMsg();
		final FindEventsRequestDto findEventsRequestDto = new FindEventsRequestDto(getCategory(msg.getCategory()), 
				dateTime(msg.getEventsFrom()), dateTime(msg.getEventsUntil()));
		
		final List<EventDto> events = execute(conn, device, findEventsRequestDto);
		return makeTheResponse(events);
	}
	
	private ResponseValuesMsg makeTheResponse(final List<EventDto> events) {
		return MsgMapper.makeResponseValues(makeProps(events), ResponseStatus.OK, "FindEvents");
	}
	
	private List<PropMsg> makeProps(final List<EventDto> events) {
		List<PropMsg> result = new ArrayList<>();
		events.forEach(event -> processEvent(result, event));
		return result;
	}

	private void processEvent(final List<PropMsg> result, final EventDto event) {
		result.add(prop("Event"));
		result.add(prop("Event code", event.getEventCode()));
		result.add(prop("Event counter", event.getEventCounter()));
		result.add(prop("Event time", event.getTimestamp().toDate()));
	}
	
	private EventLogCategoryDto getCategory(final FindEventsMsg.EventLogCatgory cat) {
		if (FindEventsMsg.EventLogCatgory.COMMUNICATION_SESSION_LOG.equals(cat)) return EventLogCategoryDto.COMMUNICATION_SESSION_LOG;
		if (FindEventsMsg.EventLogCatgory.FRAUD_DETECTION_LOG.equals(cat)) return EventLogCategoryDto.FRAUD_DETECTION_LOG;
		if (FindEventsMsg.EventLogCatgory.M_BUS_EVENT_LOG.equals(cat)) return EventLogCategoryDto.M_BUS_EVENT_LOG;
		return EventLogCategoryDto.STANDARD_EVENT_LOG;
	}


	private DateTime dateTime(final long epoch) {return new DateTime(epoch);	}
	
	//----


	private static final int CLASS_ID = 7;
	private static final int ATTRIBUTE_ID = 2;

	private static final int CLASS_ID_CLOCK = 8;
	private static final byte[] OBIS_BYTES_CLOCK = new byte[] { 0, 0, 1, 0, 0, (byte) 255 };
	private static final byte ATTRIBUTE_ID_TIME = 2;

	private static final int ACCESS_SELECTOR_RANGE_DESCRIPTOR = 1;

	private static final EnumMap<EventLogCategoryDto, ObisCode> EVENT_LOG_CATEGORY_OBISCODE_MAP = new EnumMap<>(
            EventLogCategoryDto.class);
    static {
        EVENT_LOG_CATEGORY_OBISCODE_MAP.put(EventLogCategoryDto.STANDARD_EVENT_LOG,        new ObisCode("0.0.99.98.0.255"));
        EVENT_LOG_CATEGORY_OBISCODE_MAP.put(EventLogCategoryDto.FRAUD_DETECTION_LOG,       new ObisCode("0.0.99.98.1.255"));
        EVENT_LOG_CATEGORY_OBISCODE_MAP.put(EventLogCategoryDto.COMMUNICATION_SESSION_LOG, new ObisCode("0.0.99.98.4.255"));
        EVENT_LOG_CATEGORY_OBISCODE_MAP.put(EventLogCategoryDto.M_BUS_EVENT_LOG,           new ObisCode("0.0.99.98.3.255"));
    }
	
    private DataObjectToEventListConverter dataObjectToEventListConverter = new DataObjectToEventListConverter();
    	
    
	private List<EventDto> execute(final DlmsConnection conn, final DlmsDevice device,
            final FindEventsRequestDto findEventsQuery) throws ProtocolAdapterException {

        final SelectiveAccessDescription selectiveAccessDescription = this.getSelectiveAccessDescription(
                findEventsQuery.getFrom(), findEventsQuery.getUntil());

        final AttributeAddress eventLogBuffer = new AttributeAddress(CLASS_ID,
                EVENT_LOG_CATEGORY_OBISCODE_MAP.get(findEventsQuery.getEventLogCategory()), ATTRIBUTE_ID,
                selectiveAccessDescription);

//        conn.getDlmsMessageListener()
//                .setDescription("RetrieveEvents for " + findEventsQuery.getEventLogCategory() + " from "
//                        + findEventsQuery.getFrom() + " until " + findEventsQuery.getUntil() + ", retrieve attribute: "
//                        + JdlmsObjectToStringUtil.describeAttributes(eventLogBuffer));

        GetResult getResult;
        try {
            getResult = conn.get(eventLogBuffer);
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }

        if (getResult == null) {
            throw new ProtocolAdapterException("No GetResult received while retrieving event register "
                    + findEventsQuery.getEventLogCategory());
        }

        if (!AccessResultCode.SUCCESS.equals(getResult.getResultCode())) {
            LOGGER.info("Result of getting events for {} is {}", findEventsQuery.getEventLogCategory(),
                    getResult.getResultCode());
            throw new ProtocolAdapterException("Getting the events for  " + findEventsQuery.getEventLogCategory()
                    + " from the meter resulted in: " + getResult.getResultCode());
        }

        final DataObject resultData = getResult.getResultData();
        return this.dataObjectToEventListConverter.convert(resultData, findEventsQuery.getEventLogCategory());
    }

    private SelectiveAccessDescription getSelectiveAccessDescription(final DateTime beginDateTime,
            final DateTime endDateTime) {

        final int accessSelector = ACCESS_SELECTOR_RANGE_DESCRIPTOR;

        /*
         * Define the clock object {8,0-0:1.0.0.255,2,0} to be used as
         * restricting object in a range descriptor with a from value and to
         * value to determine which elements from the buffered array should be
         * retrieved.
         */
        final DataObject clockDefinition = DataObject.newStructureData(Arrays.asList(
                DataObject.newUInteger16Data(CLASS_ID_CLOCK), DataObject.newOctetStringData(OBIS_BYTES_CLOCK),
                DataObject.newInteger8Data(ATTRIBUTE_ID_TIME), DataObject.newUInteger16Data(0)));

        final DataObject fromValue = this.dlmsHelperService.asDataObject(beginDateTime);
        final DataObject toValue = this.dlmsHelperService.asDataObject(endDateTime);

        /*
         * Retrieve all captured objects by setting selectedValues to an empty
         * array.
         */
        final DataObject selectedValues = DataObject.newArrayData(Collections.<DataObject> emptyList());

        final DataObject accessParameter = DataObject.newStructureData(Arrays.asList(clockDefinition, fromValue,
                toValue, selectedValues));

        return new SelectiveAccessDescription(accessSelector, accessParameter);
    }

}
