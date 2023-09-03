import 'package:moxplatform/moxplatform.dart';
import 'package:moxplatform_platform_interface/moxplatform_platform_interface.dart';

class AndroidPlatformImplementation extends PlatformImplementation {
  @override
  Future<String> getCacheDataPath() {
    return MoxplatformInterface.api.getCacheDataPath();
  }

  @override
  Future<String> getPersistentDataPath() {
    return MoxplatformInterface.api.getPersistentDataPath();
  }

  @override
  Future<bool> isIgnoringBatteryOptimizations() {
    return MoxplatformInterface.api.isIgnoringBatteryOptimizations();
  }

  @override
  Future<void> openBatteryOptimisationSettings() {
    return MoxplatformInterface.api.openBatteryOptimisationSettings();
  }

  @override
  Future<bool> generateVideoThumbnail(
    String src,
    String dest,
    int width,
  ) async {
    return MoxplatformInterface.api.generateVideoThumbnail(src, dest, width);
  }
}
