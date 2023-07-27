import 'package:moxplatform_platform_interface/moxplatform_platform_interface.dart';

class AndroidNotificationsImplementation extends NotificationsImplementation {
  final NotificationsImplementationApi _api = NotificationsImplementationApi();


  @override
  Future<void> createNotificationChannel(String title, String id, bool urgent) async {
    return _api.createNotificationChannel(title, id, urgent);
  }


  @override
  Future<void> showMessagingNotification(MessagingNotification notification) async {
    return _api.showMessagingNotification(notification);
  }
}
