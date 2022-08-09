import "dart:convert";
import "dart:ui";
import "dart:isolate";

import "package:moxplatform_generic/service_generic.dart";

import "package:flutter/widgets.dart";
import "package:logging/logging.dart";
import "package:get_it/get_it.dart";
import "package:moxplatform/types.dart";
import "package:moxlib/awaitabledatasender.dart";
import "package:moxplatform_platform_interface/src/isolate.dart";
import "package:moxplatform_platform_interface/src/service.dart";

class BackgroundServiceDataSender extends AwaitableDataSender<BackgroundCommand, BackgroundEvent> {
  final SendPort _port;
  BackgroundServiceDataSender(this._port) : super();

  @override
  Future<void> sendDataImpl(DataWrapper data) async {
    _port.send(jsonEncode(data.toJson()));
  }
}

Future<void> genericEntrypoint(List<dynamic> parameters) async {
  print("genericEntrypoint: Called on new Isolate");
  //WidgetsFlutterBinding.ensureInitialized();

  SendPort port = parameters[0];
  final int entrypointHandle = parameters[1];
  final entrypointCallbackHandle = CallbackHandle.fromRawHandle(entrypointHandle);
  final entrypoint = PluginUtilities.getCallbackFromHandle(entrypointCallbackHandle);
  final int handleUIEventHandle = parameters[2];
  final handleUIEventCallbackHandle = CallbackHandle.fromRawHandle(handleUIEventHandle);
  final handleUIEvent = PluginUtilities.getCallbackFromHandle(handleUIEventCallbackHandle);

  final srv = GenericBackgroundService(port);
  GetIt.I.registerSingleton<BackgroundService>(srv);
  srv.init(
    entrypoint! as Future<void> Function(),
    handleUIEvent! as Future<void> Function(Map<String, dynamic>? data)
  );
}

class GenericIsolateHandler extends IsolateHandler {
  final Logger _log;
  ReceivePort? _isolateReceivePort;
  SendPort? _isolateSendPort;
  Isolate? _isolate;
  BackgroundServiceDataSender? _dataSender;

  GenericIsolateHandler()
    : _log = Logger("GenericIsolateHandler"),
      super();

  @override
  Future<void> attach(Future<void> Function(Map<String, dynamic>? data) handleIsolateEvent) async {
    if (_isolateReceivePort != null) {
      _isolateReceivePort!.listen((data) async {
          if (data is SendPort) {
            _isolateSendPort = data;
            _dataSender = BackgroundServiceDataSender(data);
            return;
          }

          await handleIsolateEvent(jsonDecode(data as String));
      });
    } else {
      _log.severe("attach: _isolate is null");
    }
  }

  @override
  Future<void> start(
    Future<void> Function() entrypoint,
    Future<void> Function(Map<String, dynamic>? data) handleUIEvent,
    Future<void> Function(Map<String, dynamic>? data) handleIsolateEvent
  ) async {
    _log.finest("Called start");
    WidgetsFlutterBinding.ensureInitialized();

    _isolateReceivePort = ReceivePort();
    _dataSender = BackgroundServiceDataSender(_isolateReceivePort!.sendPort);
    _isolate = await Isolate.spawn<List<dynamic>>(
      genericEntrypoint,
      [
        _isolateReceivePort!.sendPort,
        PluginUtilities.getCallbackHandle(entrypoint)!.toRawHandle(),
        PluginUtilities.getCallbackHandle(handleUIEvent)!.toRawHandle()
      ]
    );

    attach(handleIsolateEvent);

    _log.finest("Service successfully started");
  }

  @override
  Future<bool> isRunning() async {
    return _isolate != null;
  }

  @override
  BackgroundServiceDataSender getDataSender() => _dataSender!;
}
