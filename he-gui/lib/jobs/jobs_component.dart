import 'dart:async';
// import 'dart:html';
import 'package:angular2/core.dart';
import 'package:angular2/router.dart';
//import 'package:angular2/platform/common.dart';
import 'package:angular2_components/angular2_components.dart';
import 'package:angular2_components/src/components/material_ripple/material_ripple.dart';

import '../model/job.dart';
import 'job_detail_component.dart';
import '../service/jobs_service.dart';
import '../pipe/app_pipes.dart';
import '../event/app_events.dart';
import '../app_constants.dart';

@Component(
    selector: 'my-jobs',
    templateUrl: 'jobs_component.html',
    styleUrls: const ['jobs_component.css', '../app.css'],
    pipes: const [IntToDateTime],
    directives: const [materialDirectives, JobDetailComponent, MaterialRippleComponent],
    providers: const [materialProviders])

class JobsComponent implements OnInit {
  List<Job> jobs;
  Job selectedJob;

  @Input()
    int height = 500;

  final JobsService _jobsService;
  final Router _router;

  JobsComponent(this._jobsService, this._router);

  Future<Null> getJobs() async {
    jobs = await _jobsService.getJobs();
  }

  void ngOnInit() {
    getJobs();

    AppConstants.eventBus.on(AddJobEvent).listen((AddJobEvent event) {
      jobs.add(event.job);
    });

    //setupGrid();
  }

  void onSelect(Job job) {
    selectedJob = job;
    gotoDetail();
  }

  void gotoDetail() {
    _router.navigate([
      'JobDetail',
      {'id': selectedJob.id.toString()}
    ]);
  }

  void addJob() {
    _router.navigate(['AddJob']);
  }
}
