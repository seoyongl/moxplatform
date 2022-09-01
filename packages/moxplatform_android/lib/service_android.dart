import "dart:convert";
import "dart:ui";

import "package:moxplatform_platform_interface/src/service.dart";
import "package:moxplatform/types.dart";
import "package:moxlib/awaitabledatasender.dart";
import "package:flutter/material.dart";
import "package:flutter/services.dart";
import "package:logging/logging.dart";
import "package:uuid/uuid.dart";
import "package:meta/meta.dart";

class AndroidBackgroundService extends BackgroundService {
  @internal
  static const MethodChannel channel = MethodChannel("me.polynom.moxplatform_android_bg");
  final Logger _log;

  AndroidBackgroundService()
    : _log = Logger("AndroidBackgroundService"),
      super();

  @override
  void setNotification(String title, String body) {
    channel.invokeMethod(
      "setNotificationBody",
      [ body ]
    );
  }

  @override
  void sendEvent(BackgroundEvent event, { String? id }) {
    final data = DataWrapper(
      id ?? const Uuid().v4(),
      event
    );
    // NOTE: *S*erver to *F*oreground
    _log.fine("S2F: ${data.toJson().toString()}");
    channel.invokeMethod("sendData", jsonEncode(data.toJson()));
  }

  @override
  void init(
    Future<void> Function() entrypoint,
    Future<void> Function(Map<String, dynamic>? data) handleEvent
  ) {
    WidgetsFlutterBinding.ensureInitialized();

    // Ensure that all native plugins are registered against this FlutterEngine, so that
    // we can use path_provider, notifications, ...
    DartPluginRegistrant.ensureInitialized();

    // Register the event handler
    channel.setMethodCallHandler((MethodCall call) async {
        await handleEvent(jsonDecode(call.arguments));
    });
    
    setNotification("Moxxy", "Preparing...");

    _log.finest("Running...");

    entrypoint();
  }
}
