import 'abstract_action.dart';
import '../../enum/request_type.dart';

class GetFirmwareVersionActionMsg extends AbstractAction {

  GetFirmwareVersionActionMsg();

  AbstractAction fromJson(Map<String, dynamic> job) {
    return new GetFirmwareVersionActionMsg.buildFromJson(job);
  }

  @override
  Map toJson() => { };

  @override RequestType requestType() => RequestType.GET_FIRMWARE_VERSION;
  @override String jsonName() => 'getFirmwareVersionMsg';
  @override String description() => "Get firmware version ";

  factory GetFirmwareVersionActionMsg.buildFromJson(Map<String, dynamic> job) {
    GetFirmwareVersionActionMsg r = new GetFirmwareVersionActionMsg();
    return r;
  }

  @override isValid() => true;
}
