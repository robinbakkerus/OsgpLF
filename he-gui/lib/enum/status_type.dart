enum StatusType {
  NOT_SET, OK, NOT_OK, OK_WITH_ERRORS, CREATED, ACCEPTED, SUBMITTED
}

class StatusTypeName {
  static StatusType typeFromName(String type) {
    if (type == null) return null;

    switch (type) {
      case 'OK': return StatusType.OK;
      case 'NOT_OK' : return StatusType.NOT_OK;
      case 'OK_WITH_ERRORS' : return StatusType.OK_WITH_ERRORS;
      case 'CREATED' : return StatusType.CREATED;
      case 'ACCEPTED' : return StatusType.ACCEPTED;
      case 'SUBMITTED' : return StatusType.SUBMITTED;
    }
    return StatusType.NOT_SET;
  }

}
