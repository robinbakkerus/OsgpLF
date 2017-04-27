import 'dart:async';

import 'package:angular2/core.dart';
import 'package:angular2/router.dart';

import '../model/job.dart';
import '../service/jobs_service.dart';

@Component(
    selector: 'my-dashboard',
    templateUrl: 'dashboard_component.html',
    styleUrls: const ['dashboard_component.css'],
    directives: const [ROUTER_DIRECTIVES])
class DashboardComponent implements OnInit {
  List<Job> jobs;

  final JobsService _heroService;

  DashboardComponent(this._heroService);

  Future<Null> ngOnInit() async {
    jobs = (await _heroService.getJobs()).skip(1).take(4).toList();
  }
}
