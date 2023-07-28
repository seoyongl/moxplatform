import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(
  PigeonOptions(
    dartOut: 'packages/moxplatform_platform_interface/lib/src/api.g.dart',
    //kotlinOut: 'packages/moxplatform_android/android/src/main/java/me/polynom/moxplatform_android/Notifications.g.kt',
    //kotlinOptions: KotlinOptions(
    //  package: 'me.polynom.moxplatform_android',
    //),
    javaOut: 'packages/moxplatform_android/android/src/main/java/me/polynom/moxplatform_android/Api.java',
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
  const MessagingNotification(this.title, this.id, this.jid, this.messages, this.channelId);

  /// The title of the conversation.
  final String title;

  /// The id of the notification.
  final int id;

  /// The id of the notification channel the notification should appear on.
  final String channelId;

  /// The JID of the chat in which the notifications happen.
  final String jid;

  /// Messages to show.
  final List<NotificationMessage?> messages;
}

enum NotificationEventType {
  markAsRead,
  reply,
  open,
}

class NotificationEvent {
  const NotificationEvent(
    this.jid,
    this.type,
    this.payload,
  );

  /// The JID the notification was for.
  final String jid;

  /// The type of event.
  final NotificationEventType type;

  /// An optional payload.
  /// - type == NotificationType.reply: The reply message text.
  /// Otherwise: undefined.
  final String? payload;
}

class NotificationI18nData {
  const NotificationI18nData(this.reply, this.markAsRead, this.you);

  /// The content of the reply button.
  final String reply;

  /// The content of the "mark as read" button.
  final String markAsRead;

  /// The text to show when *you* reply.
  final String you;
}

@HostApi()
abstract class MoxplatformApi {
  void createNotificationChannel(String title, String id, bool urgent, NotificationI18nData i18n);

  void showMessagingNotification(MessagingNotification notification);

  String getPersistentDataPath();

  String getCacheDataPath();

  void eventStub(NotificationEvent event);
}
