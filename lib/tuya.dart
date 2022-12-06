
import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:tuya/tuya_exceptions.dart';

class TuyaSdk {
  static const platform = MethodChannel('goviral.ma/Tuya');

  static Future<void> initialize() async {
    // Initialize TuyaSdk
    await platform.invokeMethod('init');
  }

  static Future<bool> sendVerificationCode(String email, countryCode) async {
    // send verification code
    try {
      bool result = await platform.invokeMethod(
          'sendVerificationCode', {"email": email, "countryCode": countryCode});
      return result;
    } on PlatformException catch (e) {
      throw TuyaUserAlreadyExistsException("${e.message}");
    }
  }

  static Future<Map<String, dynamic>?> registerWithEmail(
      String email, password, countryCode, verificationCode) async {
    try {
      return json.decode(await platform.invokeMethod('registerWithEmail', {
        "email": email,
        "password": password,
        "countryCode": countryCode,
        "verificationCode": verificationCode
      }));
    } on PlatformException catch (e) {
      throw TuyaCreateAccountException(e.message ?? "");
    }
  }

  static Future<Map<String, dynamic>?> loginWithEmail(
      String? email, String? password, String? countryCode) async {
    try {
      return json.decode(await platform.invokeMethod('loginWithEmail',
          {"email": email, "password": password, "countryCode": countryCode}));
    } on PlatformException catch (e) {
      throw TuyaLoginException(e.message ?? "");
    }
  }

  /// Create new home
  /// Swtich to new home
  static Future<Map<String, dynamic>?> createNewHome(String name) async {
    try {
      return json
          .decode(await platform.invokeMethod('createNewHome', {"name": name}));
    } on PlatformException catch (e) {
      throw TuyaCreateNewHomeException(e.message ?? "");
    }
  }

  // todo get homes

  ///connectEZMode
  /// returns device informations
  static Future<Map<String, dynamic>?> connectEZMode(
    String homeId,
    String ssid,
    String password,
  ) async {
    try {
      return json.decode(await platform.invokeMethod('connectEZMode',
          {"homeId": homeId, "ssid": ssid, "password": password}));
    } on PlatformException catch (e) {
      throw TuyaConnectEZModeException(e.message ?? "");
    }
  }

  /// getDeviceList
  static Future<List<dynamic>?> getDeviceList(String homeId) async {
    try {
      return json.decode(
          await platform.invokeMethod('getDeviceList', {"homeId": homeId}));
    } on PlatformException catch (e) {
      throw TuyaGetDeviceException(e.message ?? "");
    }
  }

  // publishDps
  static Future<bool> publishDps(String devId, String cmd) async {
    try {
      bool result = await platform
          .invokeMethod('publishDps', {"devId": devId, "cmd": cmd});
      return result;
    } on PlatformException catch (e) {
      throw TuyaPublishDpsException(e.message ?? "");
    }
  }

  /// device callbacks
  static registerDeviceCallbacks(
      Function(String, String) onDpUpdate,
      Function(String) onRemoved,
      Function(String, bool) onStatusChanged,
      Function(String, bool) onNetworkStatusChanged,
      Function(String) onDevInfoUpdate) {
    platform.setMethodCallHandler((MethodCall call) async {
      switch (call.method) {
        case 'onDpUpdate':
          dynamic result = json.decode(call.arguments);
          onDpUpdate(result["devId"], result["dpStr"]);
          break;
        case 'onRemoved':
          dynamic result = json.decode(call.arguments);
          onRemoved(result["devId"]);
          break;
        case 'onStatusChanged':
          dynamic result = json.decode(call.arguments);
          onStatusChanged(result["devId"], result["online"]);
          break;
        case 'onNetworkStatusChanged':
          dynamic result = json.decode(call.arguments);
          onNetworkStatusChanged(result["devId"], result["status"]);
          break;
        case 'onDevInfoUpdate':
          dynamic result = json.decode(call.arguments);
          onDevInfoUpdate(result["devId"]);
          break;
        default:
          throw MissingPluginException();
      }
    });
  }
}
