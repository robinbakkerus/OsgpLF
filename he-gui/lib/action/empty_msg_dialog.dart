// import 'dart:html';
import 'package:angular2/core.dart';
import 'package:angular2_components/angular2_components.dart';

@Component(
    selector: 'empty-msg-dialog',
    templateUrl: 'empty_msg_dialog.html',
    styleUrls: const ['../app.css'],
    directives: const [materialDirectives],
    providers: const [materialProviders] )

class EmptyMsgDialog  {
  EmptyMsgDialog();

  @Input() Object action;

  @Output() EventEmitter closeActionDialogEvent = new EventEmitter();
  void closeDialog() {
    closeActionDialogEvent.emit(true);
  }
}
