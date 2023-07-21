import 'dart:convert';
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:logging/logging.dart';
import 'package:meta/meta.dart';
import 'package:moxlib/moxlib.dart';
import 'package:moxplatform/moxplatform.dart';
import 'package:moxplatform_platform_interface/moxplatform_platform_interface.dart';
import 'package:uuid/uuid.dart';

class AndroidBackgroundService extends BackgroundService {
  AndroidBackgroundService()
      : _log = Logger('AndroidBackgroundService'),
        super();

  @internal
  static const MethodChannel channel =
      MethodChannel('me.polynom.moxplatform_android_bg');
  final Logger _log;

  @override
  void setNotification(String title, String body) {
    channel.invokeMethod<void>(
      'setNotificationBody',
      [body],
    );
  }

  @override
  void sendEvent(BackgroundEvent event, {String? id}) {
    final data = DataWrapper(
      id ?? const Uuid().v4(),
      event,
    );
    // NOTE: *S*erver to *F*oreground
    _log.fine('S2F: ${data.toJson()}');
    channel.invokeMethod<void>('sendData', jsonEncode(data.toJson()));
  }

  @override
  void init(
    Future<void> Function() entrypoint,
    Future<void> Function(Map<String, dynamic>? data) handleEvent,
  ) {
    WidgetsFlutterBinding.ensureInitialized();

    // Ensure that all native plugins are registered against this FlutterEngine, so that
    // we can use path_provider, notifications, ...
    DartPluginRegistrant.ensureInitialized();

    // Register the event handler
    channel.setMethodCallHandler((MethodCall call) async {
      final args = call.arguments! as String;
      await handleEvent(jsonDecode(args) as Map<String, dynamic>);
    });

    setNotification('Moxxy', 'Preparing...');

    _log.finest('Running...');

    entrypoint();
  }
}
