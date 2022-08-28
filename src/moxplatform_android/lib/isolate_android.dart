import "dart:convert";
import "dart:ui";

import "package:moxplatform_android/service_android.dart";

import "package:flutter/widgets.dart";
import "package:flutter/services.dart";
import "package:logging/logging.dart";
import "package:moxplatform_platform_interface/src/isolate.dart";
import "package:moxplatform_platform_interface/src/service.dart";
import "package:moxplatform/types.dart";
import "package:moxlib/awaitabledatasender.dart";
import "package:get_it/get_it.dart";

/// An [AwaitableDataSender] that uses flutter_background_service.
class BackgroundServiceDataSender extends AwaitableDataSender<BackgroundCommand, BackgroundEvent> {
  final MethodChannel _channel;
  BackgroundServiceDataSender() : _channel = MethodChannel("me.polynom.moxplatform_android"), super();
 
  @override
  Future<void> sendDataImpl(DataWrapper data) async {
    await _channel.invokeMethod("sendData", jsonEncode(data.toJson()));
  }
}

Future<void> androidEntrypoint() async {
  print("androidEntrypoint: Called on new FlutterEngine");
  WidgetsFlutterBinding.ensureInitialized();

  /*
  AndroidBackgroundService.channel.setMethodCallHandler((MethodCall call) async {
      print(call.method);
  });*/
  
  final data = jsonDecode(await AndroidBackgroundService.channel.invokeMethod("getExtraData", []));
  final int entrypointHandle = data["genericEntrypoint"]!;
  final entrypointCallbackHandle = CallbackHandle.fromRawHandle(entrypointHandle);
  final entrypoint = PluginUtilities.getCallbackFromHandle(entrypointCallbackHandle);
  final int handleUIEventHandle = data["eventHandle"]!;
  final handleUIEventCallbackHandle = CallbackHandle.fromRawHandle(handleUIEventHandle);
  final handleUIEvent = PluginUtilities.getCallbackFromHandle(handleUIEventCallbackHandle);

  final srv = AndroidBackgroundService();
  GetIt.I.registerSingleton<BackgroundService>(srv);
  srv.init(
    entrypoint! as Future<void> Function(),
    handleUIEvent! as Future<void> Function(Map<String, dynamic>? data)
  );
}

/// The Android specific implementation of the [IsolateHandler].
class AndroidIsolateHandler extends IsolateHandler {
  final BackgroundServiceDataSender _dataSender;
  final MethodChannel _channel;
  final Logger _log;

  AndroidIsolateHandler()
    : _channel = MethodChannel("me.polynom.moxplatform_android"),
      _dataSender = BackgroundServiceDataSender(),
      _log = Logger("AndroidIsolateHandler"),
      super();

  @override
  Future<void> attach(Future<void> Function(Map<String, dynamic>? data) handleIsolateEvent) async {
    _channel.setMethodCallHandler((MethodCall call) async {
        await handleIsolateEvent(jsonDecode(call.arguments));
    });
  }
      
  @override
  Future<void> start(
    Future<void> Function() entrypoint,
    Future<void> Function(Map<String, dynamic>? data) handleUIEvent,
    Future<void> Function(Map<String, dynamic>? data) handleIsolateEvent
  ) async {
    _log.finest("Called start");
    WidgetsFlutterBinding.ensureInitialized();

    final androidEntryHandle = PluginUtilities.getCallbackHandle(androidEntrypoint)!.toRawHandle();
    _log.finest('AndroidEntryHandle: $androidEntryHandle');
    await _channel.invokeMethod("configure", [
        androidEntryHandle,
        jsonEncode({
            "genericEntrypoint": PluginUtilities.getCallbackHandle(entrypoint)!.toRawHandle(),
            "eventHandle": PluginUtilities.getCallbackHandle(handleUIEvent)!.toRawHandle()
        }),
    ]);

    await attach(handleIsolateEvent);

    final result = await _channel.invokeMethod("start", []);
    if (result) {
      _log.finest("Service successfully started");
    } else {
      _log.severe("Service failed to start");
    }
  }

  @override
  Future<bool> isRunning() async {
    WidgetsFlutterBinding.ensureInitialized();
    return await _channel.invokeMethod("isRunning", []);
  }

  @override
  BackgroundServiceDataSender getDataSender() => _dataSender;
}
