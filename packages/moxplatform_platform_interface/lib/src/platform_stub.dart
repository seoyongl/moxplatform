import 'package:moxplatform_platform_interface/src/platform.dart';

class StubPlatformImplementation extends PlatformImplementation {
  /// Returns the path where persistent data should be stored.
  Future<String> getPersistentDataPath() async => "";

  /// Returns the path where cache data should be stored.
  Future<String> getCacheDataPath() async => "";
}
