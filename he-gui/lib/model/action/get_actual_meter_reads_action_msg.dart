import 'abstract_action.dart';
import '../../enum/request_type.dart';

class GetActualMeterReadsActionMsg extends AbstractAction {

  GetActualMeterReadsActionMsg();

  AbstractAction fromJson(Map<String, dynamic> job) {
    return new GetActualMeterReadsActionMsg.buildFromJson(job);
  }

  @override
  Map toJson() => { };

  @override RequestType requestType() => RequestType.GET_ACTUAL_METER_READS;
  @override String jsonName() => RequestTypeName.ACTION_GET_ACTUAL_METER_READS;
  @override String description() => "Get actual meter reads ";

  factory GetActualMeterReadsActionMsg.buildFromJson(Map<String, dynamic> job) {
    GetActualMeterReadsActionMsg r = new GetActualMeterReadsActionMsg();
    return r;
  }

  @override isValid() => true;
}
