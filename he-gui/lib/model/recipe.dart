import '../util/json_utils.dart';
import 'action/dlms_action_msg.dart';

class Recipe {
  int id;
  String name;
  int creationTime;
  String modificationTime;
  List<DlmsActionMsg> actions = new List();


  Recipe(this.id, this.name, this.creationTime, this.modificationTime, this.actions);

  factory Recipe.fromJson(Map<String, dynamic> recipe) {
      return new Recipe(_toInt(recipe['id']), recipe['name'],
        _toInt(recipe['creationTime']), recipe['modificationTime'],
        _toActions(recipe['actions']));
  }

  factory Recipe.newRecipe() {
    DateTime dt = new DateTime.now();
    return new Recipe(dt.millisecondsSinceEpoch, "", dt.millisecondsSinceEpoch, null, new List());
  }


  Map toJson() {
    return {
      'id'            : id,
      'name'          :_toStr(name),
      'creationTime'  :creationTime,
      'modificationTime'  :modificationTime,
      'actions'       :_parseActions()
    };
  }

  String toString() => toJson().toString();

  String _parseActions() {
    var sb = JsonUtils.startJsonArray('actions');
    for (var i=0; i<actions.length; i++) {
      DlmsActionMsg action = actions[i];
      sb.write(action.toJson());
      if (i < actions.length-1) sb.write(",");
    };
    JsonUtils.endJsonArray(sb);
    return sb.toString();
  }
}

List<DlmsActionMsg> _toActions(src) {
  List<DlmsActionMsg> r = new List();
  if (src != null) {
    for (var item in src) {
      DlmsActionMsg a = new DlmsActionMsg.fromJson(item);
      r.add(a);
    }
  }
  return r;
}

int _toInt(src) => JsonUtils.toInt(src);
String _toStr(src) => JsonUtils.toJsonString(src);
