import 'stats_details.dart';

class Statistics {
  String name;
  StatDetails requests;
  StatDetails responses;

  Statistics(this.name, this.requests, this.responses);

  factory Statistics.fromJson(Map<String, dynamic> map) {
      return new Statistics(map['name'],
        _toStatDetails(map['requests']), _toStatDetails(map['responses']));
  }
}

StatDetails _toStatDetails(src) {
  if (src != null) {
    return new StatDetails.fromJson(src);
  }
  return null;
}
