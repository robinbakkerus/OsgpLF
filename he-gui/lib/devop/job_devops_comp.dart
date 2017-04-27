
import 'package:angular2/core.dart';
// import 'package:angular2/platform/common.dart';
import 'package:angular2_components/angular2_components.dart';

import '../model/devop_summary.dart';
import '../model/job.dart';
import '../devop/devop_detail_comp.dart';

@Component(
    selector: 'my-job-devops',
    templateUrl: 'job_devops_comp.html',
    styleUrls: const ['job_devops_comp.css','../app.css'],
    directives: const [materialDirectives, DevOpDetailComponent],
    providers: const [materialProviders]
  )

class JobDevOpsComponent implements OnInit {
  @Input()
  Job job;

  DevOpSummary selectedDevOp;

  bool showActions = false;

  // final Router _router;

  JobDevOpsComponent();

  void ngOnInit() {}

  void onSelect(DevOpSummary devop) {
    selectedDevOp = devop;
    gotoDetail();
  }

  void gotoDetail()  {
    showActions = true;
  }
}
