import 'package:moxplatform_platform_interface/src/notifications.g.dart';
import 'package:moxplatform_platform_interface/src/notifications.dart';

class StubNotificationsImplementation extends NotificationsImplementation {
  @override
  Future<void> createNotificationChannel(String title, String id, bool urgent) async {}

  @override
  Future<void> showMessagingNotification(MessagingNotification notification) async {}
}
