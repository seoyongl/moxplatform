import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'moxplatform_android_platform_interface.dart';

/// An implementation of [MoxplatformAndroidPlatform] that uses method channels.
class MethodChannelMoxplatformAndroid extends MoxplatformAndroidPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('moxplatform_android');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
