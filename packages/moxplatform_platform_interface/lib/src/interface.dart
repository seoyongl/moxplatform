import 'package:moxplatform_platform_interface/src/contacts.dart';
import 'package:moxplatform_platform_interface/src/contacts_stub.dart';
import 'package:moxplatform_platform_interface/src/crypto.dart';
import 'package:moxplatform_platform_interface/src/crypto_stub.dart';
import 'package:moxplatform_platform_interface/src/isolate.dart';
import 'package:moxplatform_platform_interface/src/isolate_stub.dart';
import 'package:moxplatform_platform_interface/src/media.dart';
import 'package:moxplatform_platform_interface/src/media_stub.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

abstract class MoxplatformInterface extends PlatformInterface {
  MoxplatformInterface() : super(token: _token);

  static final Object _token = Object();

  static IsolateHandler handler = StubIsolateHandler();
  static MediaScannerImplementation media = StubMediaScannerImplementation();
  static CryptographyImplementation crypto = StubCryptographyImplementation();
  static ContactsImplementation contacts = StubContactsImplementation();

  /// Return the current platform name.
  Future<String?> getPlatformName();
}
