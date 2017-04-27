// import 'dart:developer';
import 'dart:async';
import 'package:angular2/core.dart';
import 'package:angular2_components/angular2_components.dart';

import '../service/jobs_service.dart';
import '../model/devop_summary.dart';
import '../model/response_msg.dart';
import '../pipe/app_pipes.dart';

@Component(
    selector: 'my-devop-detail',
    templateUrl: 'devop_detail_comp.html',
    styleUrls: const ['../app.css'],
    pipes: const[PropsmsgPrettyPrint],
    directives: const [materialDirectives],
    providers: const [materialProviders]
  )

class DevOpDetailComponent {

  final JobsService _jobsService;
  // final Router _router;

  DevOpDetailComponent(this._jobsService);

  DevOpSummary _devop;
  ResponseMsg response = new ResponseMsg("", null, null, );

  @Input('devop')
  set devop(DevOpSummary devop) {
    _devop = devop;
    getResponse(devop.correlId);
  }
  get devop =>_devop;

  Future<Null> getResponse(String correlId) async {
    this.response = await _jobsService.getResponse(correlId);
  }


}
