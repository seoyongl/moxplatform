import "package:moxplatform_platform_interface/moxplatform_platform_interface.dart";
import "package:moxplatform_platform_interface/src/isolate.dart";
import "package:moxplatform_platform_interface/src/media.dart";

class MoxplatformPlugin {
  static IsolateHandler get handler => MoxplatformInterface.handler;
  static MediaScannerImplementation get media => MoxplatformInterface.media;
}