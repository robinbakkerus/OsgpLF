enum PropertyType {
  DEFAULT, STRING, INT, REAL, DATE, TEXT, BOOL
}

class PropertyTypeName {
  static PropertyType typeFromName(String type) {
    // if (type == null) return PropertyType.DEFAULT;
    //
    // switch (type) {
    //   case 'STRING': return PropertyType.STRING;
    //   case 'INT' : return PropertyType.INT;
    //   case 'REAL' : return PropertyType.REAL;
    //   case 'DATE': return PropertyType.DATE;
    //   case 'TEXT' : return PropertyType.TEXT;
    //   case 'BOOL' : return PropertyType.BOOL;
    // }
    return PropertyType.DEFAULT;
  }
}
