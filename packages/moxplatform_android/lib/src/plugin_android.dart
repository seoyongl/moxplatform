import 'package:moxplatform_android/src/isolate_android.dart';
import 'package:moxplatform_android/src/media_android.dart';
import 'package:moxplatform_platform_interface/moxplatform_platform_interface.dart';

class MoxplatformAndroidPlugin extends MoxplatformInterface {
  static void registerWith() {
    // ignore: avoid_print
    print('MoxplatformAndroidPlugin: Registering implementation');
    MoxplatformInterface.handler = AndroidIsolateHandler();
    MoxplatformInterface.media = AndroidMediaScannerImplementation();
  }

  @override
  Future<String> getPlatformName() async => 'Android';
}
