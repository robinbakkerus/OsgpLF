import '../../util/json_utils.dart';
import '../../util/date_utils.dart';
import 'abstract_action.dart';
import '../../enum/request_type.dart';

class LoadProfileActionMsg extends AbstractAction {

  String obisCode;
  int dateFrom;
  int dateTo;

  LoadProfileActionMsg();
  factory LoadProfileActionMsg.fromValues(String obisCode, int dateFrom, int dateTo) {
    LoadProfileActionMsg result = new LoadProfileActionMsg();
    result.obisCode = obisCode;
    result.dateFrom = dateFrom;
    result.dateTo = dateTo;
    return result;
  }

  AbstractAction fromJson(Map<String, dynamic> job) {
    return new LoadProfileActionMsg.buildFromJson(job);
  }

  @override String description() => "Load Profile";
  @override String jsonName() => RequestTypeName.ACTION_PROFILE_GENERIC_DATA;

  @override
  Map toJson() {
    return {
      "requestType": "PROFILE_GENERIC_DATA",
      "profileGenericDataMsg": {
        "obisCode": this.obisCode,
        "dateFrom": _actualDate(this.dateFrom),
        "dateTo": _actualDate(this.dateTo)
      }
    };
  }
  @override
  RequestType requestType() => RequestType.FINDEVENTS;

  factory LoadProfileActionMsg.buildFromJson(Map<String, dynamic> inputMap) {
    Map<String, dynamic> map = inputMap[RequestTypeName.ACTION_PROFILE_GENERIC_DATA];
    return new LoadProfileActionMsg.fromValues(
      (map['obisCode']), _toInt(map['dateFrom']), _toInt(map['dateTo']));
  }

  @override
  String toString() {
    return 'reqType:FINDEVENT, cat: ' + obisCode.toString() + ' from:' +
       dateFrom.toString() + ' to: ' + dateTo.toString();
  }

  @override isValid() {
    return this.obisCode != null && this.dateFrom != null && this.dateTo != null;
  }
}

int _toInt(s) => JsonUtils.toInt(s);
int _actualDate(n) => DateUtils.specialDateIntToActualInt(n);
