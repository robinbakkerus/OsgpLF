import 'dart:developer';

import 'package:angular2/core.dart';
import 'package:intl/intl.dart';
import '../enum/request_type.dart';
import '../model/props_msg.dart';

@Pipe(name: 'int_to_date')
class IntToDateTime extends PipeTransform {
  String transform(val, [List args]) {
    
    if (val != null) {
      DateTime dt = new DateTime.fromMillisecondsSinceEpoch(val);
      var formatter = new DateFormat('dd-MM-yyyy');
      return formatter.format(dt);
    } else {
      return "";
    }
  }
}

@Pipe(name: 'pretty_request_type_name')
class RequestTypePrettyPrint extends PipeTransform {
  String transform(RequestType val, [List args]) {
    if (val != null) {
      return RequestTypeName.prettyName(val);
    } else {
      return "";
    }
  }
}

@Pipe(name: 'pretty_props_value')
class PropsmsgPrettyPrint extends PipeTransform {
  String transform(PropsMsg val, [List args]) {
    if (val != null) {
      return PropsMsg.prettyPrint(val);
    } else {
      return "";
    }
  }
}
