import "package:moxlib/awaitabledatasender.dart";
import "package:moxplatform_platform_interface/src/isolate.dart";

class StubDataSender extends AwaitableDataSender {
  StubDataSender() : super();

  @override
  Future<void> sendDataImpl(DataWrapper data) async {}
}

class StubIsolateHandler extends IsolateHandler {
  final StubDataSender _sender;

  StubIsolateHandler() : _sender = StubDataSender();

  @override
  Future<void> attach(
    Future<void> Function(Map<String, dynamic>? data) handleIsolateEvent
  ) async {
    print("STUB ATTACHED!!!!!!");
  }
  
  @override
  Future<void> start(
    Future<void> Function() entrypoint,
    Future<void> Function(Map<String, dynamic>? data) handleUIEvent,
    Future<void> Function(Map<String, dynamic>? data) handleIsolateEvent
  ) async {
    print("STUB STARTED!!!!!!");
  }

  @override
  Future<bool> isRunning() async => false;

  @override
  AwaitableDataSender getDataSender() => _sender;
}
