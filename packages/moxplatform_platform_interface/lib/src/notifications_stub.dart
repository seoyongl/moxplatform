import 'dart:async';
import 'package:moxplatform_platform_interface/src/api.g.dart';
import 'package:moxplatform_platform_interface/src/notifications.dart';

class StubNotificationsImplementation extends NotificationsImplementation {
  @override
  Future<void> createNotificationChannels(
    List<NotificationChannel> channels,
  ) async {}

  @override
  Future<void> deleteNotificationChannels(
    List<String> ids,
  ) async {}

  Future<void> createNotificationGroups(
    List<NotificationGroup> groups,
  ) async {}

  @override
  Future<void> deleteNotificationGroups(
    List<String> ids,
  ) async {}

  @override
  Future<void> showMessagingNotification(
    MessagingNotification notification,
  ) async {}

  @override
  Future<void> showNotification(RegularNotification notification) async {}

  @override
  Future<void> dismissNotification(int id) async {}

  @override
  Future<void> setNotificationSelfAvatar(String path) async {}

  @override
  Future<void> setI18n(NotificationI18nData data) async {}

  @override
  Stream<NotificationEvent> getEventStream() {
    return StreamController<NotificationEvent>().stream;
  }
}
