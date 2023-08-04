import 'dart:convert';
import 'dart:ui';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:get_it/get_it.dart';
import 'package:logging/logging.dart';
import 'package:moxlib/moxlib.dart';
import 'package:moxplatform/moxplatform.dart';
import 'package:moxplatform_android/src/service_android.dart';

/// An [AwaitableDataSender] that uses flutter_background_service.
class BackgroundServiceDataSender
    extends AwaitableDataSender<BackgroundCommand, BackgroundEvent> {
  BackgroundServiceDataSender()
      : _channel = const MethodChannel('me.polynom.moxplatform_android'),
        super();
  final MethodChannel _channel;

  @override
  Future<void> sendDataImpl(DataWrapper data) async {
    await _channel.invokeMethod<void>('sendData', jsonEncode(data.toJson()));
  }
}

@pragma('vm:entry-point')
Future<void> androidEntrypoint() async {
  // ignore: avoid_print
  print('androidEntrypoint: Called on new FlutterEngine');
  WidgetsFlutterBinding.ensureInitialized();

  /*
  AndroidBackgroundService.channel.setMethodCallHandler((MethodCall call) async {
      print(call.method);
  });*/

  final result = await AndroidBackgroundService.channel.invokeMethod<String>(
    'getExtraData',
    <void>[],
  );
  final data = jsonDecode(result!) as Map<String, dynamic>;
  final entrypointHandle = data['genericEntrypoint']! as int;
  final entrypointCallbackHandle =
      CallbackHandle.fromRawHandle(entrypointHandle);
  final entrypoint =
      PluginUtilities.getCallbackFromHandle(entrypointCallbackHandle);
  final handleUIEventHandle = data['eventHandle']! as int;
  final handleUIEventCallbackHandle =
      CallbackHandle.fromRawHandle(handleUIEventHandle);
  final handleUIEvent =
      PluginUtilities.getCallbackFromHandle(handleUIEventCallbackHandle);

  final srv = AndroidBackgroundService();
  GetIt.I.registerSingleton<BackgroundService>(srv);
  srv.init(
    entrypoint! as Future<void> Function(),
    handleUIEvent! as Future<void> Function(Map<String, dynamic>? data),
  );
}

/// The Android specific implementation of the [IsolateHandler].
class AndroidIsolateHandler extends IsolateHandler {
  AndroidIsolateHandler()
      : _channel = const MethodChannel('me.polynom.moxplatform_android'),
        _dataSender = BackgroundServiceDataSender(),
        _log = Logger('AndroidIsolateHandler'),
        super();
  final BackgroundServiceDataSender _dataSender;
  final MethodChannel _channel;
  final Logger _log;

  @override
  Future<void> attach(
    Future<void> Function(Map<String, dynamic>? data) handleIsolateEvent,
  ) async {
    _channel.setMethodCallHandler((MethodCall call) async {
      final args = call.arguments as String;
      await handleIsolateEvent(jsonDecode(args) as Map<String, dynamic>);
    });
  }

  @override
  Future<void> start(
    Future<void> Function() entrypoint,
    Future<void> Function(Map<String, dynamic>? data) handleUIEvent,
    Future<void> Function(Map<String, dynamic>? data) handleIsolateEvent,
  ) async {
    _log.finest('Called start');
    WidgetsFlutterBinding.ensureInitialized();

    final androidEntrypointHandle =
        PluginUtilities.getCallbackHandle(androidEntrypoint)!.toRawHandle();
    _log.finest('androidEntrypointHandle: $androidEntrypointHandle');
    await _channel.invokeMethod<void>('configure', <dynamic>[
      androidEntrypointHandle,
      jsonEncode({
        'genericEntrypoint':
            PluginUtilities.getCallbackHandle(entrypoint)!.toRawHandle(),
        'eventHandle':
            PluginUtilities.getCallbackHandle(handleUIEvent)!.toRawHandle()
      }),
    ]);

    await attach(handleIsolateEvent);

    final result = await _channel.invokeMethod<bool>('start', <void>[]);
    if (result == true) {
      _log.finest('Service successfully started');
    } else {
      _log.severe('Service failed to start');
    }
  }

  @override
  Future<bool> isRunning() async {
    WidgetsFlutterBinding.ensureInitialized();
    return (await _channel.invokeMethod<bool>('isRunning', <void>[])) == true;
  }

  @override
  BackgroundServiceDataSender getDataSender() => _dataSender;
}
