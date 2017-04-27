
class DevOpSummary {
  String deviceId;
  String correlId;
  String responseSummary;
  String status;

  DevOpSummary(this.deviceId, this.correlId, this.responseSummary, this.status);

  factory DevOpSummary.fromJson(Map<String, dynamic> map) {
      return new DevOpSummary(map['deviceId'], map['correlId'],  map['responseSummary'], map['status']  );
    }
}
