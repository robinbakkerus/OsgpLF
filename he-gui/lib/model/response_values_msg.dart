// import '../util/json_utils.dart';
import 'props_msg.dart';
import '../enum/status_type.dart';

class ResponseValuesMsg {
  String action;
  String code;
  StatusType status;

  List<PropsMsg> properties;

  ResponseValuesMsg(this.action, this.code, this.status, this.properties);

  factory ResponseValuesMsg.fromJson(Map<String, dynamic> map) {
      return new ResponseValuesMsg(map['action'], map['code'],
        _toStatus(map),  _toProps(map['properties']));
    }
}

List<PropsMsg> _toProps(map) {
  List<PropsMsg> r = new List();
  if (map != null) {
    for (var item in map) {
      PropsMsg a = new PropsMsg.fromJson(item);
      r.add(a);
    }
  }
  return r;
}

// int _toInt(s) => JsonUtils.toInt(s);
StatusType _toStatus(map) {return StatusTypeName.typeFromName(map['status']);}
