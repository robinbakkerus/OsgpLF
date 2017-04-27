// import '../util/json_utils.dart';
import '../util/json_utils.dart';

class DeviceGroup {
  int id;
  String name;
  String description;

  DeviceGroup(this.id, this.name, this.description);

  factory DeviceGroup.fromJson(Map<String, dynamic> map) {
    return new DeviceGroup(_toInt(map['id']), map['name'], map['description']);
  }
}

int _toInt(src) => JsonUtils.toInt(src);
