import 'abstract_action.dart';
import '../../enum/request_type.dart';

class GetSpecificObjectActionMsg extends AbstractAction {

  GetSpecificObjectActionMsg();

  AbstractAction fromJson(Map<String, dynamic> job) {
    return new GetSpecificObjectActionMsg.buildFromJson(job);
  }

  @override
  Map toJson() => { };

  @override RequestType requestType() => RequestType.GET_SPECIFIC_OBJECT;
  @override String jsonName() => RequestTypeName.ACTION_GET_SPECIFIC_OBJECT;
  @override String description() => "Get specific object ";

  factory GetSpecificObjectActionMsg.buildFromJson(Map<String, dynamic> job) {
    GetSpecificObjectActionMsg r = new GetSpecificObjectActionMsg();
    return r;
  }

  @override isValid() => true;
}
