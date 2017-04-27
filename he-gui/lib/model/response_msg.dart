// import '../util/json_utils.dart';
import 'response_values_msg.dart';
import '../enum/status_type.dart';

class ResponseMsg {
  String action;
  StatusType status;
  List<ResponseValuesMsg> values;

  ResponseMsg(this.action, this.status, this.values);

  factory ResponseMsg.fromJson(Map<String, dynamic> map) {
    return new ResponseMsg(map['action'], _toStatus(map), _toProperties(map['values']));
  }
}

List<ResponseValuesMsg> _toProperties(map) {
  List<ResponseValuesMsg> r = new List();
  if (map != null) {
    for (var item in map) {
      ResponseValuesMsg p = new ResponseValuesMsg.fromJson(item);
      r.add(p);
    }
  }
  return r;
}

StatusType _toStatus(map) {return StatusTypeName.typeFromName(map['status']);}
