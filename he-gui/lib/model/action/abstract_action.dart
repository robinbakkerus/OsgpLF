import '../../enum/request_type.dart';

abstract class AbstractAction {
  AbstractAction();
  RequestType requestType();
  String description();
  String jsonName();
  // AbstractAction fromJson(Map<String, dynamic> job);
  Map toJson();
  bool isValid();
}
