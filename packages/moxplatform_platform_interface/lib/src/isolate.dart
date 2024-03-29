import 'package:moxlib/moxlib.dart';

/// A class abstracting the interaction between the UI isolate and the background
/// service, which is either a regular isolate or an Android foreground service.
/// This class only deals with the direction of UI -> Service.
abstract class IsolateHandler {
  /// Start the background service.
  /// [entrypoint] is the entrypoint that is run inside the new isolate.
  /// [handleUIEvent] is a handler function that is called when the isolate receives data from the UI.
  /// [handleIsolateEvent] is a handler function that is called when the UI receives data from the service.
  /// [initialLocale] the locale to pass to the background service when first starting.
  Future<void> start(
    Future<void> Function(String initialLocale) entrypoint,
    Future<void> Function(Map<String, dynamic>? data) handleUIEvent,
    Future<void> Function(Map<String, dynamic>? data) handleIsolateEvent,
    String initialLocale,
  );

  /// Make sure that the UI event handler is registered without starting the isolate.
  Future<void> attach(
    Future<void> Function(Map<String, dynamic>? data) handleIsolateEvent,
  );

  /// Return true if the background service is running. False if it's not.
  Future<bool> isRunning();

  /// Return the [AwaitableDataSender] for communicating with the service.
  AwaitableDataSender getDataSender();
}
