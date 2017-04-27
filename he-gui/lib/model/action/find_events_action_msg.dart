//import 'dart:developer';
import '../../util/json_utils.dart';
import '../../util/date_utils.dart';
import 'abstract_action.dart';
import '../../enum/request_type.dart';

class FindEventsActionMsg extends AbstractAction {

  String category;
  int eventsFrom;
  int eventsUntil;

  FindEventsActionMsg();
  factory FindEventsActionMsg.fromValues(String category, int eventsFrom, int eventsUntil) {
    FindEventsActionMsg result = new FindEventsActionMsg();
    result.category = category;
    result.eventsFrom = eventsFrom;
    result.eventsUntil = eventsUntil;
    return result;
  }

  AbstractAction fromJson(Map<String, dynamic> job) {
    return new FindEventsActionMsg.buildFromJson(job);
  }

  @override String description() => "Find events";
  @override String jsonName() => RequestTypeName.ACTION_FINDEVENTS;

  @override
  Map toJson() {
    return {
      "requestType": "FINDEVENTS",
      "findEventsMsg": {
        "category": this.category,
        "eventsFrom": _actualDate(this.eventsFrom),
        "eventsUntil": _actualDate(this.eventsUntil)
      }
    };
  }
  @override
  RequestType requestType() => RequestType.FINDEVENTS;

  factory FindEventsActionMsg.buildFromJson(Map<String, dynamic> inputMap) {
    Map<String, dynamic> map = inputMap[RequestTypeName.ACTION_FINDEVENTS];
    return new FindEventsActionMsg.fromValues(
      (map['category']), _toInt(map['eventsFrom']), _toInt(map['eventsUntil']));
  }

  @override
  String toString() {
    return 'reqType:FINDEVENT, cat: ' + category.toString() + ' from:' +
       eventsFrom.toString() + ' to: ' + eventsUntil.toString();
  }

  @override isValid() {
    return category != null && eventsFrom != null && eventsUntil != null;
  }
}

int _toInt(s) => JsonUtils.toInt(s);
int _actualDate(n) => DateUtils.specialDateIntToActualInt(n);
