import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(
  PigeonOptions(
    dartOut: 'packages/moxplatform_platform_interface/lib/src/notifications.g.dart',
    //kotlinOut: 'packages/moxplatform_android/android/src/main/java/me/polynom/moxplatform_android/Notifications.g.kt',
    //kotlinOptions: KotlinOptions(
    //  package: 'me.polynom.moxplatform_android',
    //),
    javaOut: 'packages/moxplatform_android/android/src/main/java/me/polynom/moxplatform_android/Notifications.java',
    javaOptions: JavaOptions(
      package: 'me.polynom.moxplatform_android',
    ),
  ),
)
class NotificationMessageContent {
  const NotificationMessageContent(
    this.body,
    this.mime,
    this.path,
  );

  /// The textual body of the message.
  final String? body;

  /// The path and mime type of the media to show.
  final String? mime;
  final String? path;
}

class NotificationMessage {
  const NotificationMessage(
    this.sender,
    this.content,
    this.jid,
    this.timestamp,
    this.avatarPath,
  );

  /// The sender of the message.
  final String sender;

  /// The jid of the sender.
  final String jid;

  /// The body of the message.
  final NotificationMessageContent content;

  /// Milliseconds since epoch.
  final int timestamp;

  /// The path to the avatar to use
  final String? avatarPath;
}

class MessagingNotification {
  const MessagingNotification(this.title, this.id, this.messages, this.channelId);

  /// The title of the conversation.
  final String title;

  /// The id of the notification.
  final int id;

  /// The id of the notification channel the notification should appear on.
  final String channelId;

  /// Messages to show.
  final List<NotificationMessage?> messages;
}

@HostApi()
abstract class NotificationsImplementationApi {
  void createNotificationChannel(String title, String id, bool urgent);

  void showMessagingNotification(MessagingNotification notification);
}
