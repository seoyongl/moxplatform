import 'dart:async';
import 'package:moxplatform_platform_interface/src/api.g.dart';
import 'package:moxplatform_platform_interface/src/notifications.dart';

class StubNotificationsImplementation extends NotificationsImplementation {
  @override
  Future<void> createNotificationChannel(String title, String id, bool urgent) async {}

  @override
  Future<void> showMessagingNotification(MessagingNotification notification) async {}

  @override
  Stream<NotificationEvent> getEventStream() {
    return StreamController<NotificationEvent>().stream;
  }
}
