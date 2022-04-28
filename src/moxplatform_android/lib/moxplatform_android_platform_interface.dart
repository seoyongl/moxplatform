import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'moxplatform_android_method_channel.dart';

abstract class MoxplatformAndroidPlatform extends PlatformInterface {
  /// Constructs a MoxplatformAndroidPlatform.
  MoxplatformAndroidPlatform() : super(token: _token);

  static final Object _token = Object();

  static MoxplatformAndroidPlatform _instance = MethodChannelMoxplatformAndroid();

  /// The default instance of [MoxplatformAndroidPlatform] to use.
  ///
  /// Defaults to [MethodChannelMoxplatformAndroid].
  static MoxplatformAndroidPlatform get instance => _instance;
  
  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [MoxplatformAndroidPlatform] when
  /// they register themselves.
  static set instance(MoxplatformAndroidPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
