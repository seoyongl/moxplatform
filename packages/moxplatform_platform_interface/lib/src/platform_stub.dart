import 'package:moxplatform_platform_interface/src/api.g.dart';
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

  @override
  Future<bool> generateVideoThumbnail(
    String src,
    String dest,
    int width,
  ) async =>
      false;

  @override
  Future<List<String>> pickFiles(FilePickerType type, bool pickMultiple) async => [];
}
