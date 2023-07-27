import 'package:moxplatform_platform_interface/src/notifications.g.dart';

abstract class NotificationsImplementation {
  Future<void> createNotificationChannel(String title, String id, bool urgent);

  Future<void> showMessagingNotification(MessagingNotification notification);
}
