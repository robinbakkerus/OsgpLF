class JsonUtils {

  static int toInt(var s) {
    if (s != null) {
      return s is int ? s : int.parse(s);
    } else {
      return null;
    }
  }

  static double toReal(var s) {
    if (s != null) {
      return s is double ? s : double.parse(s);
    } else {
      return null;
    }
  }

  static DateTime toDateTime(var s) {
    if (s != null) {
      return s is DateTime ? s : DateTime.parse(s);
    } else {
      return null;
    }
  }

  static DateTime intToDate(int n) {
    if (n != null) {
      return new DateTime.fromMicrosecondsSinceEpoch(n);
    } else {
      return null;
    }
  }

  static StringBuffer startJson() {
		StringBuffer sb = new StringBuffer();
		sb.write(r'{');
		return sb;
	}

	static String endJson(StringBuffer sb) {
		sb.write('}');
		return sb.toString();
	}

	static StringBuffer startJsonArray(String name) {
		StringBuffer sb = new StringBuffer();
		sb.write('[');
		return sb;
	}

	static String endJsonArray(StringBuffer sb) {
		sb.write(']');
		return sb.toString();
	}


  static String toJsonString(String s) => '"' + s + '"';
}
