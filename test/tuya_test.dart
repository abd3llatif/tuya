import 'package:flutter_test/flutter_test.dart';
import 'package:tuya/tuya.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockTuyaPlatform
    with MockPlatformInterfaceMixin {
}

void main() {
  // final TuyaPlatform initialPlatform = TuyaPlatform.instance;

  // test('$MethodChannelTuya is the default instance', () {
  //   expect(initialPlatform, isInstanceOf<MethodChannelTuya>());
  // });

  // test('getPlatformVersion', () async {
  //   Tuya tuyaPlugin = Tuya();
  //   MockTuyaPlatform fakePlatform = MockTuyaPlatform();
  //   TuyaPlatform.instance = fakePlatform;

  //   expect(await tuyaPlugin.getPlatformVersion(), '42');
  // });
}
