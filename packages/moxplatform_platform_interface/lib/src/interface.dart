import 'package:moxplatform_platform_interface/src/api.g.dart';
import 'package:moxplatform_platform_interface/src/contacts.dart';
import 'package:moxplatform_platform_interface/src/contacts_stub.dart';
import 'package:moxplatform_platform_interface/src/crypto.dart';
import 'package:moxplatform_platform_interface/src/crypto_stub.dart';
import 'package:moxplatform_platform_interface/src/isolate.dart';
import 'package:moxplatform_platform_interface/src/isolate_stub.dart';
import 'package:moxplatform_platform_interface/src/notifications.dart';
import 'package:moxplatform_platform_interface/src/notifications_stub.dart';
import 'package:moxplatform_platform_interface/src/platform.dart';
import 'package:moxplatform_platform_interface/src/platform_stub.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

abstract class MoxplatformInterface extends PlatformInterface {
  MoxplatformInterface() : super(token: _token);

  static final Object _token = Object();

  static MoxplatformApi api = MoxplatformApi();

  static IsolateHandler handler = StubIsolateHandler();
  static CryptographyImplementation crypto = StubCryptographyImplementation();
  static ContactsImplementation contacts = StubContactsImplementation();
  static NotificationsImplementation notifications =
      StubNotificationsImplementation();
  static PlatformImplementation platform = StubPlatformImplementation();

  /// Return the current platform name.
  Future<String?> getPlatformName();
}
