
import '../util/json_utils.dart';
import 'action/dlms_action_msg.dart';
import 'devop_summary.dart';

class Job {
  final int id;
  String name;
  int deviceGroupId;
  int recipeId;
  int filterId;
  String deviceGroup;
  String recipe;
  String filter;
  int creationTime;
  int modificationTime;
  int devopsTotal;
  int devopsSuccess;
  int devopsFailed;
  String organisation;
  String status;
  List<DlmsActionMsg> actions;
  List<DevOpSummary> devops;

  Job(this.id, this.name,
    this.deviceGroupId, this.recipeId, this.filterId,
    this.deviceGroup, this.recipe, this.filter,
    this.creationTime, this.modificationTime, this.organisation,
    this.devopsTotal, this.devopsSuccess, this.devopsFailed,
    this.status, this.actions, this.devops);

  factory Job.fromJson(Map<String, dynamic> job) {
      Job r = new Job(_toInt(job['id']), job['name'],
        _toInt(job['deviceGroupId']), _toInt(job['recipeId']), _toInt(job['filterId']),
        job['deviceGroup'], job['recipe'], job['filter'],
         _toInt(job['creationTime']), _toInt(job['modificationTime']), job['organisation'],
        _toInt(job['devopsTotal']), _toInt(job['devopsSuccess']), _toInt(job['devopsFailed']),
        job['status'], _toActions(job['actions']), _toDevOps(job['devops']));
      return r;
  }

  factory Job.newJob() {
    DateTime dt = new DateTime.now();
    return new Job(
      dt.millisecondsSinceEpoch, null,
      null, null, null,
      null, null, null,
      dt.millisecondsSinceEpoch, null, "Infostroom", //TODO
      0, 0, 0,
      "NOT_SET", null, null);
  }


  Map toJson() {
    return {
      'id'              : this.id,
      'name'            :_toStr(this.name),
      'creationTime'    :this.creationTime,
      'recipeId'        :this.recipeId,
      'deviceGroupId'   :this.deviceGroupId,
      'filterId'        :this.filterId,
      'modificationTime' :this.modificationTime,
      'actions'         :_parseActions()
    };
  }

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

  String toString() => toJson().toString();
}

int _toInt(s) => JsonUtils.toInt(s);
String _toStr(s) => JsonUtils.toJsonString(s);

List<DlmsActionMsg> _toActions(s) {
  List<DlmsActionMsg> r = new List();
//  for (var item in s) {
//    DevOpSummary a = new DevOpSummary.fromJson(item);
//    r.add(a);
//  }
  return r;
}

List<DevOpSummary> _toDevOps(src) {
  List<DevOpSummary> r = new List();
  if (src != null) {
    for (var item in src) {
      DevOpSummary a = new DevOpSummary.fromJson(item);
      r.add(a);
   }
  }
  return r;
}
