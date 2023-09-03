import 'package:moxplatform_android/src/contacts_android.dart';
import 'package:moxplatform_android/src/crypto_android.dart';
import 'package:moxplatform_android/src/isolate_android.dart';
import 'package:moxplatform_android/src/platform_android.dart';
import 'package:moxplatform_platform_interface/moxplatform_platform_interface.dart';

class MoxplatformAndroidPlugin extends MoxplatformInterface {
  static void registerWith() {
    // ignore: avoid_print
    print('MoxplatformAndroidPlugin: Registering implementation');
    MoxplatformInterface.contacts = AndroidContactsImplementation();
    MoxplatformInterface.crypto = AndroidCryptographyImplementation();
    MoxplatformInterface.handler = AndroidIsolateHandler();
    MoxplatformInterface.platform = AndroidPlatformImplementation();
  }

  @override
  Future<String> getPlatformName() async => 'Android';
}
