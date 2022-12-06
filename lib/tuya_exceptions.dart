class TuyaUserAlreadyExistsException implements Exception {
  String message;
  TuyaUserAlreadyExistsException(this.message);

  @override
  String toString() {
    return message;
  }
}

class TuyaCreateAccountException implements Exception {
  String message;
  TuyaCreateAccountException(this.message);

  @override
  String toString() {
    return message;
  }
}


class TuyaLoginException implements Exception {
  String message;
  TuyaLoginException(this.message);

  @override
  String toString() {
    return message;
  }
}


class TuyaCreateNewHomeException implements Exception {
  String message;
  TuyaCreateNewHomeException(this.message);

  @override
  String toString() {
    return message;
  }
}


class TuyaConnectEZModeException implements Exception {
  String message;
  TuyaConnectEZModeException(this.message);

  @override
  String toString() {
    return message;
  }
}

class TuyaGetDeviceException implements Exception {
  String message;
  TuyaGetDeviceException(this.message);

  @override
  String toString() {
    return message;
  }
}


class TuyaPublishDpsException implements Exception {
  String message;
  TuyaPublishDpsException(this.message);

  @override
  String toString() {
    return message;
  }
}