import "package:moxplatform_generic/isolate_generic.dart";

import "package:moxplatform_platform_interface/moxplatform_platform_interface.dart";

class MoxplatformLinuxPlugin extends MoxplatformInterface {
  static void registerWith() {
    print("MoxplatformLinuxPlugin: Registering implementation");
    MoxplatformInterface.handler = GenericIsolateHandler();
  }

  @override
  Future<String> getPlatformName() async => "Linux";
}
