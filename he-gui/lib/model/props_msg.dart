// import 'dart:developer';
import '../enum/prop_value_type.dart';

class PropsMsg {
  String key;
  PropertyType type;
  String value;
  String units;

  PropsMsg(this.key, this.type, this.value, this.units);

  factory PropsMsg.fromJson(Map<String, dynamic> map) {
      return new PropsMsg(map['key'], _toType(map),
        map['value'], map['units']);
  }


  static String prettyPrint(PropsMsg prop) {
    StringBuffer sb = new StringBuffer();
    // if (prop.key != null) sb.write(prop.key);
    if (prop.key != null) sb.write(prop.value);
    return sb.toString();
  }
}

PropertyType _toType(map) {
  if (map != null) return PropertyTypeName.typeFromName(map['type']);
  return PropertyType.DEFAULT;
}
