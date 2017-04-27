import 'package:intl/intl.dart';

class DateUtils {

  // generated either a normal datestring or something like 'RunDay -7'
  static String strNrToDateStr(String s) {
    if (isNumeric(s)) {
      int n = int.parse(s);
      if (n != null) {
        if (n > 1000 || n < -1000) {
          DateTime dt = new DateTime.fromMillisecondsSinceEpoch(n);
          var formatter = new DateFormat('dd-MM-yyyy');
          return formatter.format(dt);
        } else {
          return "RunDay " + _sign(n) + " " + n.toString();
        }
      }
    }
    return "";
  }

  static String _sign(int n) => (n>0) ?  "+" : "";

  static bool isNumeric(String s) {
    if (s == null || s.isEmpty) return false;
    try {
        int.parse(s);
        return true;
      } catch(FormatException) {
        return false;
      }
    }

  // returns valid date, where n can be -1000 .. +1000
  static DateTime specialDateIntToDate(int n) {
    if (n > 1000 || n < -1000) {
      return new DateTime.fromMillisecondsSinceEpoch(n);
    } else {
      DateTime dt = new DateTime.now();
      return dt.add(new Duration(days: n));
    }
  }

  // returns valid date string, where n can be -1000 .. +1000
  static String specialDateIntToDateStr(int n) {
    if (n == null){
        // return "";
        DateTime dt = specialDateIntToDate(n);
        var formatter = new DateFormat('dd-MM-yyyy');
        return formatter.format(dt);
    } else {
      DateTime dt = specialDateIntToDate(n);
      var formatter = new DateFormat('dd-MM-yyyy');
      return formatter.format(dt);
    }
  }

  // returns a valid int given the string, this may return a value between -1000 .. +10000
  static int dateStrToInt(String s) {
    if (isNumeric(s)) {
      return int.parse(s);
    } else {
      return null;
    }
  }


  // returns correct epoch, also if n is between -1000 .. +1000
  static int specialDateIntToActualInt(int n) {
    DateTime dt = specialDateIntToDate(n);
    return dt.millisecondsSinceEpoch;
  }
}
