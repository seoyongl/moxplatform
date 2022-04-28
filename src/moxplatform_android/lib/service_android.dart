import "dart:ui";

import "package:moxplatform_platform_interface/src/service.dart";
import "package:moxplatform/types.dart";
import "package:moxlib/awaitabledatasender.dart";
import "package:flutter/material.dart";
import "package:logging/logging.dart";
import "package:uuid/uuid.dart";
import "package:flutter_background_service_android/flutter_background_service_android.dart";

class AndroidBackgroundService extends BackgroundService {
  final Logger _log;
  final AndroidServiceInstance _srv;

  AndroidBackgroundService(AndroidServiceInstance srv)
    : _srv = srv,
      _log = Logger("AndroidBackgroundService"),
      super();

  @override
  void setNotification(String title, String body) {
    _srv.setForegroundNotificationInfo(title: title, content: body);
  }

  @override
  void sendEvent(BackgroundEvent event, { String? id }) {
    final data = DataWrapper(
      id ?? const Uuid().v4(),
      event
    );
    // NOTE: *S*erver to *F*oreground
    _log.fine("S2F: ${data.toJson().toString()}");
    _srv.invoke("event", data.toJson());
  }

  @override
  void init(
    Future<void> Function() entrypoint,
    void Function(Map<String, dynamic>? data) handleEvent
  ) {
    WidgetsFlutterBinding.ensureInitialized();

    // Ensure that all native plugins are registered against this FlutterEngine, so that
    // we can use path_provider, notifications, ...
    DartPluginRegistrant.ensureInitialized();

    _srv.on("command").listen(handleEvent);
    setNotification("Moxxy", "Preparing...");

    _log.finest("Running...");

    entrypoint();
  }
}
