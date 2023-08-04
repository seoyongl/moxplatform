import 'package:moxplatform_platform_interface/src/platform.dart';

class StubPlatformImplementation extends PlatformImplementation {
  /// Returns the path where persistent data should be stored.
  @override
  Future<String> getPersistentDataPath() async => '';

  /// Returns the path where cache data should be stored.
  @override
  Future<String> getCacheDataPath() async => '';

  @override
  Future<bool> isIgnoringBatteryOptimizations() async => false;

  @override
  Future<void> openBatteryOptimisationSettings() async {}
}
