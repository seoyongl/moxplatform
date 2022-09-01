import "package:moxplatform/types.dart";

abstract class BackgroundService {
  /// Set the notification of the background service, if available
  void setNotification(String title, String body);

  /// Send data from the background service to the UI.
  void sendEvent(BackgroundEvent event, { String? id });

  /// Called before [entrypoint]. Sets up whatever it needs to set up.
  /// [handleEvent] is a function that is called whenever the service receives
  /// data.
  void init(
    Future<void> Function() entrypoint,
    Future<void> Function(Map<String, dynamic>? data) handleEvent
  );
}
