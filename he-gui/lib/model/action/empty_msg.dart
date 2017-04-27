import 'abstract_action.dart';
import '../../enum/request_type.dart';

class EmptyMsg extends AbstractAction {

  @override RequestType requestType() => RequestType.DUMMY;
  @override String jsonName() => 'EmptyMsg';

  EmptyMsg();

  @override String description() => "Empty message";

  factory EmptyMsg.fromJson(Map<String, dynamic> job) {
    EmptyMsg r = new EmptyMsg();
    return r;
  }

  Map toJson() => { };

  @override isValid() => true;
}
