//import 'dart:developer';
//import 'dart:html';
import 'package:angular2/core.dart';
import 'package:angular2_components/angular2_components.dart';

import '../model/action/find_events_action_msg.dart';
import '../pipe/app_pipes.dart';
import '../app_constants.dart';
import '../model/action/dlms_action_msg.dart';
import '../util/date_input_comp.dart';

@Component(
    selector: 'find-events-dialog',
    templateUrl: 'find_event_dialog.html',
    styleUrls: const ['../app.css'],
    pipes: const[IntToDateTime],
    directives: const [materialDirectives, DateInputComp],
    providers: const [materialProviders] )

class FindEventDialog  {

  FindEventsActionMsg findEvents = new FindEventsActionMsg();

  @Input('action')
  set action(DlmsActionMsg value) {
    findEvents = value.action;
  }

  String title = "FindEvents";

  FindEventDialog();

  bool showCategories = false;
  void toggleShowCategories() {showCategories = !showCategories;}

  List<String> categories = AppConstants.findEventCategories();

  void selectCategory(String r) {
    toggleShowCategories();
    findEvents.category = r;
  }

  @Output() EventEmitter closeActionDialogEvent = new EventEmitter();

  void closeDialog() {
    closeActionDialogEvent.emit(true);
  }

}
