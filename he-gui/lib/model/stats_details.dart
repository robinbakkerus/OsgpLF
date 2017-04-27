import '../util/json_utils.dart';

class StatDetails {
  int inCount;
  int outCount;

  StatDetails(this.inCount, this.outCount);

  factory StatDetails.fromJson(Map<String, dynamic> map) {
      return new StatDetails(_toInt(map['inCount']), _toInt(map['outCount']));
  }

}

int _toInt(src) => JsonUtils.toInt(src);
