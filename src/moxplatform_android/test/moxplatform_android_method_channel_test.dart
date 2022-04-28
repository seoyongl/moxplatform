import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moxplatform_android/moxplatform_android_method_channel.dart';

void main() {
  MethodChannelMoxplatformAndroid platform = MethodChannelMoxplatformAndroid();
  const MethodChannel channel = MethodChannel('moxplatform_android');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
