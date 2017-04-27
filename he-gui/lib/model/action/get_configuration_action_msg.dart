import 'abstract_action.dart';
import '../../enum/request_type.dart';

class GetConfigurationActionMsg extends AbstractAction {

  GetConfigurationActionMsg();

  AbstractAction fromJson(Map<String, dynamic> job) {
    return new GetConfigurationActionMsg.buildFromJson(job);
  }

  @override
  Map toJson() {
    return {
      "requestType": "GET_CONFIGURATION",
      "getConfigurationMsg": {
      }
    };
  }

  @override  RequestType requestType() => RequestType.GET_CONFIGURATION;
  @override String jsonName() => RequestTypeName.ACTION_GET_CONFIGURATION;
  @override String description() => "Get configuration ";

  factory GetConfigurationActionMsg.buildFromJson(Map<String, dynamic> job) {
    GetConfigurationActionMsg r = new GetConfigurationActionMsg();
    return r;
  }

  @override isValid() => true;
}
