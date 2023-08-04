import 'dart:async';
import 'package:moxplatform_platform_interface/src/api.g.dart';

abstract class NotificationsImplementation {
  /// Creates a notification channel with the name [title] and id [id]. If [urgent] is true, then
  /// it configures the channel as carrying urgent information.
  Future<void> createNotificationChannel(
    String title,
    String description,
    String id,
    bool urgent,
  );

  /// Shows a notification [notification] in the messaging style with everyting it needs.
  Future<void> showMessagingNotification(MessagingNotification notification);

  /// Shows a regular notification [notification].
  Future<void> showNotification(RegularNotification notification);

  /// Dismisses the notification with id [id].
  Future<void> dismissNotification(int id);

  /// Sets the path to the self-avatar for in-notification replies.
  Future<void> setNotificationSelfAvatar(String path);

  /// Configures the i18n data for usage in notifications.
  Future<void> setI18n(NotificationI18nData data);

  Stream<NotificationEvent> getEventStream();
}
