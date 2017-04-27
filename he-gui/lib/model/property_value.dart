
class PropValue {
  String devid;
  String correlId;
  String responseSummary;

  PropValue(this.devid, this.correlId, this.responseSummary);

  factory PropValue.fromJson(Map<String, dynamic> map) {
      return new PropValue(map['deviceId'], map['correlId'],  map['responseSummary']  );
    }
}
