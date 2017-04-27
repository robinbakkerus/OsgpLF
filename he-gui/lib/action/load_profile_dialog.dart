//import 'dart:developer';
//import 'dart:html';
import 'package:angular2/core.dart';
import 'package:angular2_components/angular2_components.dart';

import '../model/action/load_profile_action_msg.dart';
import '../pipe/app_pipes.dart';
import '../model/action/dlms_action_msg.dart';
import '../util/date_input_comp.dart';

@Component(
    selector: 'load-profile-dialog',
    templateUrl: 'load_profile_dialog.html',
    styleUrls: const ['../app.css'],
    pipes: const[IntToDateTime],
    directives: const [materialDirectives, DateInputComp],
    providers: const [materialProviders] )

class LoadProfileDialog  {

  LoadProfileActionMsg loadProfile = new LoadProfileActionMsg();

  @Input('action')
  set action(DlmsActionMsg value) {
    this.loadProfile = value.action;
  }

  String title = "Load Profile";

  LoadProfileDialog();

  @Output() EventEmitter closeActionDialogEvent = new EventEmitter();

  void closeDialog() {
    closeActionDialogEvent.emit(true);
  }

}
