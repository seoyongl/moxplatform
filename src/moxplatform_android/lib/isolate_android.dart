import "dart:convert";
import "dart:ui";

import "package:moxplatform_android/service_android.dart";

import "package:flutter/widgets.dart";
import "package:logging/logging.dart";
import "package:get_it/get_it.dart";
import "package:flutter_background_service/flutter_background_service.dart";
import "package:flutter_background_service_android/flutter_background_service_android.dart";
import "package:moxplatform_platform_interface/src/isolate.dart";
import "package:moxplatform_platform_interface/src/service.dart";
import "package:moxplatform/types.dart";
import "package:moxlib/awaitabledatasender.dart";

/// An [AwaitableDataSender] that uses flutter_background_service.
class BackgroundServiceDataSender extends AwaitableDataSender<BackgroundCommand, BackgroundEvent> {
  final FlutterBackgroundService _srv;
 
  BackgroundServiceDataSender() : _srv = FlutterBackgroundService(), super();
 
  @override
  Future<void> sendDataImpl(DataWrapper data) async {
    _srv.invoke("command", data.toJson());
  }
}

void onStart(ServiceInstance instance, String extra) {
  final data = jsonDecode(extra);
  final int entrypointHandle = data["entrypointHandle"]!;
  final entrypointCallbackHandle = CallbackHandle.fromRawHandle(entrypointHandle);
  final entrypoint = PluginUtilities.getCallbackFromHandle(entrypointCallbackHandle);
  final int handleUIEventHandle = data["eventHandle"]!;
  final handleUIEventCallbackHandle = CallbackHandle.fromRawHandle(handleUIEventHandle);
  final handleUIEvent = PluginUtilities.getCallbackFromHandle(handleUIEventCallbackHandle);

  final srv = AndroidBackgroundService(instance as AndroidServiceInstance);
  GetIt.I.registerSingleton<BackgroundService>(srv);
  srv.init(
    () async => await entrypoint!(),
    (data) async => await handleUIEvent!(data)
  );
}

/// The Android specific implementation of the [IsolateHandler].
class AndroidIsolateHandler extends IsolateHandler {
  final FlutterBackgroundService _srv;
  final BackgroundServiceDataSender _dataSender;
  final Logger _log;

  AndroidIsolateHandler()
    : _srv = FlutterBackgroundService(),
      _dataSender = BackgroundServiceDataSender(),
      _log = Logger("AndroidIsolateHandler"),
      super();

  @override
  void attach(Future<void> Function(Map<String, dynamic>? data) handleIsolateEvent) {
    _srv.on("event").listen(handleIsolateEvent);
  }
      
  @override
  Future<void> start(
    Future<void> Function() entrypoint,
    Future<void> Function(Map<String, dynamic>? data) handleUIEvent,
    Future<void> Function(Map<String, dynamic>? data) handleIsolateEvent
  ) async {
    _log.finest("Called start");
    WidgetsFlutterBinding.ensureInitialized();

    _srv.on("event").listen(handleIsolateEvent);
    await _srv.configure(
      iosConfiguration: IosConfiguration(
        autoStart: true,
        onBackground: (_) => true,
        onForeground: (_, __) => true
      ),
      androidConfiguration: AndroidConfiguration(
        extraData: jsonEncode(
          {
            "entrypointHandle": PluginUtilities.getCallbackHandle(entrypoint)!.toRawHandle(),
            "eventHandle": PluginUtilities.getCallbackHandle(handleUIEvent)!.toRawHandle()
          }
        ),
        onStart: onStart,
        autoStart: true,
        isForegroundMode: true
      )
    );
    if (await _srv.startService()) {
      _log.finest("Service successfully started");
    } else {
      _log.severe("Service failed to start");
    }
  }

  @override
  Future<bool> isRunning() async {
    WidgetsFlutterBinding.ensureInitialized();

    return await _srv.isRunning();
  }

  @override
  BackgroundServiceDataSender getDataSender() => _dataSender;
}
