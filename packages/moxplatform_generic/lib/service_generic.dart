import "dart:convert";
import "dart:ui";
import "dart:isolate";

import "package:moxplatform_platform_interface/src/service.dart";
import "package:moxplatform/types.dart";
import "package:moxlib/awaitabledatasender.dart";
import "package:flutter/material.dart";
import "package:logging/logging.dart";
import "package:uuid/uuid.dart";

class GenericBackgroundService extends BackgroundService {
  final Logger _log;
  final SendPort _sender;
  late final ReceivePort _receiver;

  GenericBackgroundService(this._sender)
    : _log = Logger("GenericBackgroundService"),
      super();

  @override
  void setNotification(String title, String body) {}

  @override
  void sendEvent(BackgroundEvent event, { String? id }) {
    final data = DataWrapper(
      id ?? const Uuid().v4(),
      event
    );
    // NOTE: *S*erver to *F*oreground
    _log.fine("S2F: ${data.toJson().toString()}");
    _sender.send(jsonEncode(data.toJson()));
  }

  @override
  void init(
    Future<void> Function() entrypoint,
    Future<void> Function(Map<String, dynamic>? data) handleEvent
  ) {
    //WidgetsFlutterBinding.ensureInitialized();

    // Ensure that all native plugins are registered against this Isolate, so that
    // we can use path_provider, notifications, ...
    DartPluginRegistrant.ensureInitialized();

    _receiver = ReceivePort();
    _sender.send(_receiver.sendPort);
    
    // Register the event handler
    _receiver.listen((data) {
        final arg = jsonDecode(data as String);
        handleEvent(arg);
    });
    
    _log.finest("Running...");
    entrypoint();
  }
}
