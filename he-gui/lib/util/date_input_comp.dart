//import 'dart:developer';
// import 'dart:html';
import 'package:angular2/core.dart';
import 'package:angular2_components/angular2_components.dart';

import '../util/date_utils.dart';

@Component(
    selector: 'my-date-input',
    templateUrl: 'date_input_comp.html',
    styleUrls: const ['../app.css'],
    directives: const [materialDirectives],
    providers: const [materialProviders] )

class DateInputComp  {

  DateInputComp();

  int date;

  int _intDate;

  @Input('intDate')
  set intDate(int value) {
    _intDate = value;
    dateStr = value.toString();
  }

  @Output() EventEmitter changeValueEvent = new EventEmitter();

  get intDate => _intDate;

  String _dateStr = null;
  String get dateStr => _dateStr;
  void set dateStr(String src) {
    if (src == null || src.isEmpty) {
      _dateStr = "";
    } else {
      _dateStr = DateUtils.strNrToDateStr(src);
    }
    _intDate = DateUtils.dateStrToInt(src);
    changeValueEvent.emit(_intDate);
  }

}
