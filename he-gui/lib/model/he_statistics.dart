
import 'statistics.dart';
import '../util/json_utils.dart';

class HeStatistics {
  final List<Statistics> stats;
	final int timeSinceReset;
	final int errorCount;
	final int retryCount;

  String label1;
  String label2;
  int count;
  HeStatistics(this.stats, this.timeSinceReset, this.errorCount, this.retryCount);

  factory HeStatistics.fromJson(Map<String, dynamic> map) {
      return new HeStatistics(_toStats(map['stats']),
        _toInt(map['timeSinceReset']), _toInt(map['errorCount']), _toInt(map['retryCount']));
  }
}

List<Statistics> _toStats(src) {
  List<Statistics> r = new List();
  if (src != null) {
    for (var item in src) {
      Statistics a = new Statistics.fromJson(item);
      r.add(a);
   }
  }
  return r;
}

int _toInt(s) => JsonUtils.toInt(s);
