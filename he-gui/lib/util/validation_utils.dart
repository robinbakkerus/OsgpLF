import '../model/action/dlms_action_msg.dart';

class ValidationUtils {

  static Map<String, bool> actionClass(DlmsActionMsg dlmsAction) {
    if (dlmsAction != null && dlmsAction.action != null) {
      if (dlmsAction.action.isValid()) return ngClass("ok", true);
    }
    return ngClass(null, true);
  }

  static Map<String, bool> listClass(int len, bool showMarker) {
    String s = len > 0 ? "ok" : null;
    return ngClass(s, showMarker);
  }

  static Map<String, bool> ngClass(String src, bool showMarker) {
    final classes = {
      'ng-valid': showMarker && isValid(src),
      'ng-invalid': showMarker && !isValid(src),
      'ng-hidden': !showMarker
    };
    return classes;
  }

  static bool isValid(String s) => (s != null && !s.isEmpty);
}
