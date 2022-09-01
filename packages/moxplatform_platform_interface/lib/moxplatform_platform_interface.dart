import "package:moxplatform_platform_interface/src/isolate.dart";
import "package:moxplatform_platform_interface/src/isolate_stub.dart";
import "package:moxplatform_platform_interface/src/media.dart";
import "package:moxplatform_platform_interface/src/media_stub.dart";
import "package:plugin_platform_interface/plugin_platform_interface.dart";

abstract class MoxplatformInterface extends PlatformInterface {
  /// Constructs a MyPluginPlatform.
  MoxplatformInterface() : super(token: _token);

  static final Object _token = Object();
  
  static IsolateHandler _handler = StubIsolateHandler();
  static MediaScannerImplementation _media = StubMediaScannerImplementation();

  static IsolateHandler get handler => _handler;
  static set handler(IsolateHandler instance) {
    _handler = instance;
  }

  static MediaScannerImplementation get media => _media;
  static set media(MediaScannerImplementation instance) {
    _media = instance;
  }


  /// Return the current platform name.
  Future<String?> getPlatformName();
}
