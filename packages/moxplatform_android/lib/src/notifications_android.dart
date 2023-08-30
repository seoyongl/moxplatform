import 'dart:async';
import 'package:flutter/services.dart';
import 'package:moxplatform_platform_interface/moxplatform_platform_interface.dart';

class AndroidNotificationsImplementation extends NotificationsImplementation {
  final MoxplatformApi _api = MoxplatformApi();

  final EventChannel _channel =
      const EventChannel('me.polynom/notification_stream');

  @override
  Future<void> createNotificationChannels(
      List<NotificationChannel> channels) async {
    return _api.createNotificationChannels(channels);
  }

  @override
  Future<void> deleteNotificationChannels(List<String> ids) {
    return _api.deleteNotificationChannels(ids);
  }

  @override
  Future<void> createNotificationGroups(List<NotificationGroup> groups) async {
    return _api.createNotificationGroups(groups);
  }

  @override
  Future<void> deleteNotificationGroups(List<String> ids) {
    return _api.deleteNotificationGroups(ids);
  }

  @override
  Future<void> showMessagingNotification(
    MessagingNotification notification,
  ) async {
    return _api.showMessagingNotification(notification);
  }

  @override
  Future<void> showNotification(RegularNotification notification) async {
    return _api.showNotification(notification);
  }

  @override
  Future<void> dismissNotification(int id) async {
    return _api.dismissNotification(id);
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
