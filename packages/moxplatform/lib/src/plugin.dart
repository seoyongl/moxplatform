import 'package:moxplatform_platform_interface/moxplatform_platform_interface.dart';

class MoxplatformPlugin {
  static IsolateHandler get handler => MoxplatformInterface.handler;
  static MediaScannerImplementation get media => MoxplatformInterface.media;
  static CryptographyImplementation get crypto => MoxplatformInterface.crypto;
  static ContactsImplementation get contacts => MoxplatformInterface.contacts;
  static NotificationsImplementation get notifications => MoxplatformInterface.notifications;
}
