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
}
