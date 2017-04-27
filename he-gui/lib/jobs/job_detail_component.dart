import 'dart:async';

import 'package:angular2/core.dart';
import 'package:angular2/router.dart';
import 'package:angular2/platform/common.dart';
import 'package:angular2_components/angular2_components.dart';

import '../model/job.dart';
import '../service/jobs_service.dart';
import '../devop/job_devops_comp.dart';

@Component(
    selector: 'my-job-detail',
    templateUrl: 'job_detail_component.html',
    styleUrls: const ['job_detail_component.css'],
    directives: const [materialDirectives, JobDevOpsComponent],
    providers: const [materialProviders] )

class JobDetailComponent implements OnInit {
  Job job;
  final JobsService _jobsService;
  final RouteParams _routeParams;
  final Location _location;

  JobDetailComponent(this._jobsService, this._routeParams, this._location);

  Future<Null> ngOnInit() async {
    var _id = _routeParams.get('id');
    var id = int.parse(_id ?? '', onError: (_) => null);
    if (id != null) job = await (_jobsService.getJob(id));
  }

  void goBack() => _location.back();
}
