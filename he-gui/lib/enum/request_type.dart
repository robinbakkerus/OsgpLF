enum RequestType {
  DUMMY,  GET_CONFIGURATION, FINDEVENTS, GET_SPECIFIC_OBJECT,
    GET_ACTUAL_METER_READS, GET_FIRMWARE_VERSION, PROFILE_GENERIC_DATA
}

class RequestTypeName {
  static final String ACTION_GET_CONFIGURATION = 'getConfiguration';
  static final String ACTION_FINDEVENTS =  'findEventsMsg';
  static final String ACTION_GET_ACTUAL_METER_READS =  'getActualMeterReads';
  static final String ACTION_GET_FIRMWARE_VERSION = 'getFirmwareVersion';
  static final String ACTION_GET_SPECIFIC_OBJECT =  'getSpecificObjectMsg';
  static final String ACTION_PROFILE_GENERIC_DATA =  'profileGenericDataMsg';


  static String nameFromType(RequestType type) {
    String r;
    switch (type) {
      case RequestType.DUMMY: return '';
      case RequestType.GET_CONFIGURATION: return 'GET_CONFIGURATION';
      case RequestType.FINDEVENTS: return 'FINDEVENTS';
      case RequestType.GET_ACTUAL_METER_READS: return 'GET_ACTUAL_METER_READS';
      case RequestType.GET_FIRMWARE_VERSION: return 'GET_FIRMWARE_VERSION';
      case RequestType.GET_SPECIFIC_OBJECT: return 'GET_SPECIFIC_OBJECT';
      case RequestType.PROFILE_GENERIC_DATA: return 'PROFILE_GENERIC_DATA';
    }
    return r;
  }

  static RequestType typeFromName(String type) {
    switch (type) {
      case 'GET_CONFIGURATION': return RequestType.GET_CONFIGURATION;
      case 'FINDEVENTS' : return RequestType.FINDEVENTS;
      case 'GET_ACTUAL_METER_READS' : return RequestType.GET_ACTUAL_METER_READS;
      case 'GET_ACTUAL_METER_READS': return RequestType.GET_ACTUAL_METER_READS;
      case 'GET_SPECIFIC_OBJECT' : return RequestType.GET_SPECIFIC_OBJECT;
      case 'PROFILE_GENERIC_DATA' : return RequestType.PROFILE_GENERIC_DATA;
    }
    return RequestType.DUMMY;
  }

  static String prettyName(RequestType type) {
    String r = "??";
    switch (type.toString()) {
      case 'RequestType.DUMMY': return '';
      case 'RequestType.GET_CONFIGURATION': return 'GetConfiguration';
      case 'RequestType.FINDEVENTS': return 'FindEvents';
      case 'RequestType.GET_ACTUAL_METER_READS': return 'GetActualMeterReads';
      case 'RequestType.GET_FIRMWARE_VERSION': return 'GetFirmwareVersion';
      case 'RequestType.GET_SPECIFIC_OBJECT': return 'GetSpecificObject';
      case 'RequestType.PROFILE_GENERIC_DATA': return 'ProfileGenericDataMsg';
    }
    return r;
  }
}
