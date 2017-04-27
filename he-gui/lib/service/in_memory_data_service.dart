import 'dart:async';
import 'dart:convert';
// import 'dart:math';

import 'package:angular2/core.dart';
import 'package:http/http.dart';
import 'package:http/testing.dart';

import '../model/job.dart';
import '../model/devop_summary.dart';

@Injectable()
class InMemoryDataService extends MockClient {

  static final _initialJobs = [
      {
      "id": 1,
      "deviceGroup": 1, "name": "n1", "creationTime":"1969-07-20 20:18:00", "modificationTime":"1969-07-20 20:18:00",
      "devopsCount":100, "devopsSuccess":98, "devopsFailed":2, "status":"Done",
      "actions": [
        {
          "requestType": "GET_CONFIGURATION",
          "getConfigurationMsg": {
          }
         },
        {
          "requestType": "FINDEVENTS",
          "findEventsMsg": {
            "category": "M_BUS_EVENT_LOG",
            "eventsFrom": "1483775655476"
          }
      }]
    }
  ];


  static final List<Job> _jobsDb =
      _initialJobs.map((json) => new Job.fromJson(json)).toList();

  static final List<DevOpSummary> _devopsDb = null;
    //_initialDevOps.map((json) => new DevOpSummary.fromJson(json)).toList();

  // static int _nextId = _jobsDb.map((job) => job.id).fold(0, max) + 1;

  static Future<Response> _handler(Request request) async {
    var data;
    if (request.url.toString() == 'app/jobs') {
//        String prefix = request.url.queryParameters['name'] ?? '';
//        final regExp = new RegExp(prefix, caseSensitive: false);
//        data = _jobsDb.where((job) => job.name.contains(regExp)).toList();
          data = _jobsDb;
    } else if (request.url.toString() == "app/devops") {
      data = _devopsDb;
    } else {
      throw 'Unimplemented HTTP url ${request.url}';
    }

    return new Response(JSON.encode({'data': data}), 200,
        headers: {'content-type': 'application/json'});
  }

  InMemoryDataService() : super(_handler);
}
