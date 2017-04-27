import '../../enum/request_type.dart';
import 'abstract_action.dart';
import 'get_configuration_action_msg.dart';
import 'find_events_action_msg.dart';
import 'get_actual_meter_reads_action_msg.dart';
import 'get_specific_object_action_msg.dart';
import 'get_firmware_version_action_msg.dart';
import 'load_profile_action_msg.dart';

class DlmsActionMsg {
  final String requestType;
  final AbstractAction action;

  DlmsActionMsg(this.requestType, this.action);

  factory DlmsActionMsg.fromValues(RequestType reqType, AbstractAction action) {
    String strReqtyp = RequestTypeName.nameFromType(reqType);
    return new DlmsActionMsg(strReqtyp, action);
  }

  factory DlmsActionMsg.fromJson(Map<String, dynamic> map) {
    String strReqtyp = map['requestType'];
    RequestType reqtyp = RequestTypeName.typeFromName(strReqtyp);
    AbstractAction action = DlmsActionMsg.makeAction(reqtyp, map);
    return new DlmsActionMsg(strReqtyp, action);
  }

  Map toJson() {
    return action.toJson();
  }

  static AbstractAction makeAction(RequestType type, Map<String, dynamic> map) {
    switch (type) {
      case RequestType.DUMMY: return null;
      case RequestType.GET_CONFIGURATION: return new GetConfigurationActionMsg.buildFromJson(map);
      case RequestType.FINDEVENTS: return new FindEventsActionMsg.buildFromJson(map);
      case RequestType.GET_ACTUAL_METER_READS: return new GetActualMeterReadsActionMsg.buildFromJson(map);
      case RequestType.GET_FIRMWARE_VERSION: return new GetFirmwareVersionActionMsg.buildFromJson(map);
      case RequestType.GET_SPECIFIC_OBJECT: return new GetSpecificObjectActionMsg.buildFromJson(map);
      case RequestType.PROFILE_GENERIC_DATA: return new LoadProfileActionMsg.buildFromJson(map);
    }
    return null;
  }

  static AbstractAction makeNewAction(RequestType type) {
    switch (type) {
      case RequestType.DUMMY: return null;
      case RequestType.GET_CONFIGURATION: return new GetConfigurationActionMsg();
      case RequestType.FINDEVENTS: return new FindEventsActionMsg();
      case RequestType.GET_ACTUAL_METER_READS: return new GetActualMeterReadsActionMsg();
      case RequestType.GET_FIRMWARE_VERSION: return new GetFirmwareVersionActionMsg();
      case RequestType.GET_SPECIFIC_OBJECT: return new GetSpecificObjectActionMsg();
      case RequestType.PROFILE_GENERIC_DATA: return new LoadProfileActionMsg();
    }
    return null;
  }

 }

 /*
 List<DlmsActionMsg> _toActions(s) {
   List<DlmsActionMsg> r = new List();
   for (var item in s) {
     DlmsActionMsg a = new DlmsActionMsg.fromJson(item);
     r.add(a);
   }
   return r;
 }

 ResponseMsg _toResponse(map) {
   return new ResponseMsg.fromJson(map);
 }
  */
