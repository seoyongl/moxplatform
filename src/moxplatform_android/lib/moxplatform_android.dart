import "package:moxplatform_android/isolate_android.dart";
import "package:moxplatform_android/media_android.dart";

import "package:moxplatform_platform_interface/moxplatform_platform_interface.dart";

class MoxplatformAndroidPlugin extends MoxplatformInterface {
  static void registerWith() {
    print("========================================================================================");
    MoxplatformInterface.handler = AndroidIsolateHandler();
    MoxplatformInterface.media = AndroidMediaScannerImplementation();
  }

  @override
  Future<String> getPlatformName() async => "Android";
}
