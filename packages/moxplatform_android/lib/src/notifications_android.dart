import 'dart:async';
import 'package:flutter/services.dart';
import 'package:moxplatform_platform_interface/moxplatform_platform_interface.dart';

class AndroidNotificationsImplementation extends NotificationsImplementation {
  final MoxplatformApi _api = MoxplatformApi();

  final EventChannel _channel =
      const EventChannel('me.polynom/notification_stream');

  @override
  Future<void> createNotificationChannel(
    String title,
    String id,
    bool urgent,
  ) async {
    return _api.createNotificationChannel(title, id, urgent);
  }

  @override
  Future<void> showMessagingNotification(
    MessagingNotification notification,
  ) async {
    return _api.showMessagingNotification(notification);
  }

  @override
  Future<void> setNotificationSelfAvatar(String path) async {
    return _api.setNotificationSelfAvatar(path);
  }

  @override
  Future<void> setI18n(NotificationI18nData data) {
    return _api.setNotificationI18n(data);
  }

  @override
  Stream<NotificationEvent> getEventStream() => _channel
      .receiveBroadcastStream()
      .cast<Object>()
      .map(NotificationEvent.decode);
}
