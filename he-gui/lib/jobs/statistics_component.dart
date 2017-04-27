// import 'dart:developer';
import 'dart:async';
import 'package:angular2/core.dart';
//import 'package:angular2/platform/common.dart';
import 'package:angular2_components/angular2_components.dart';

import '../model/he_statistics.dart';
import '../model/statistics.dart';
import '../model/stats_line.dart';
import 'job_detail_component.dart';
import '../service/jobs_service.dart';
import '../pipe/app_pipes.dart';

@Component(
    selector: 'my-heStatistics',
    templateUrl: 'statistics_component.html',
    styleUrls: const ['../app.css'],
    pipes: const[IntToDateTime],
    directives: const [materialDirectives, JobDetailComponent],
    providers: const [materialProviders]
)

class StatisticsComponent implements OnInit {
  HeStatistics heStatistics;
  List<StatsLine> statLines;

  final JobsService _jobsService;
  Timer timer;

  StatisticsComponent(this._jobsService);

  void ngOnInit() {
    getStatistics();
    startMonitor();
  }


  Future<Null> getStatistics() async {
    heStatistics = await _jobsService.getStatistics();
    statLines = new List();
    statLines.add(new StatsLine("phase", "count", null));
    heStatistics.stats.forEach((stats) {
      updateStatLines(stats);
    });
    statLines.add(new StatsLine("retry count", null, heStatistics.retryCount));
    statLines.add(new StatsLine("error count", null, heStatistics.errorCount));
    statLines.add(new StatsLine("since-reset", null, heStatistics.timeSinceReset));

    if (heStatistics.stats[0] != null && heStatistics.stats[0].responses != null
        && heStatistics.stats[0].responses.inCount != null) {
      if (heStatistics.stats[0].responses.inCount >= (heStatistics.stats[0].requests.inCount)) {
        this.timer.cancel();
      }
    }
  }

  void updateStatLines(Statistics stats) {
    statLines.add(new StatsLine(stats.name, null, null));
    statLines.add(new StatsLine("request in", null, stats.requests.inCount));
    statLines.add(new StatsLine("request out", null, stats.requests.outCount));
    statLines.add(new StatsLine("response in", null, stats.responses.inCount));
    statLines.add(new StatsLine("response out", null, stats.responses.outCount));
    statLines.add(new StatsLine("", null, null));
  }

  void startMonitor() {
   const interval = 5000;
   const progressRate = const Duration(milliseconds:interval);
   timer = new Timer.periodic(progressRate, (Timer timer) => runMonitor(interval));
 }

 void runMonitor(var interval) {
   getStatistics();
 }

 void stopTimer() {
   timer.cancel();
 }

}
