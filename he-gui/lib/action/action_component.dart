// import 'dart:developer' ;
import 'package:angular2/core.dart';
import 'package:angular2/platform/common.dart';
import 'package:angular2_components/angular2_components.dart';

import '../model/job.dart';
import '../model/action/dlms_action_msg.dart';
import '../action/find_event_dialog.dart';
import '../action/load_profile_dialog.dart';
import '../action/empty_msg_dialog.dart';

@Component(
    selector: 'my-action',
    templateUrl: 'action_component.html',
    styleUrls: const ['../app.css'],
    directives: const [materialDirectives,
       FindEventDialog, EmptyMsgDialog, LoadProfileDialog],
    providers: const [materialProviders] )

class ActionComponent  {
  Job job;
  // final JobsService _jobsService;
  // final RouteParams _routeParams;
  final Location _location;

  DlmsActionMsg _dlmsAction;
  @Input('dlmsAction')
  set dlmsAction(DlmsActionMsg dlmsAction) {
      _dlmsAction = dlmsAction;
  }
  get dlmsAction => _dlmsAction;


  @Output() EventEmitter closeActionDialogEvent = new EventEmitter();

  //ActionComponent(this._jobsService, this._routeParams, this._location);
  ActionComponent(this._location);

  void goBack() => _location.back();

  Object getDlmsAction() {
    return dlmsAction;
  }

  bool showDetailAction(String actionName) {
    // debugger();
    if (dlmsAction != null) {
      String _actionType = dlmsAction.action.runtimeType.toString();
      //print('JBB actionType = ' _actionType);
      return _actionType == actionName;
    } else {
      return false;
    }
  }

  void closeActionDialog() {
    closeActionDialogEvent.emit(true);
  }

}
