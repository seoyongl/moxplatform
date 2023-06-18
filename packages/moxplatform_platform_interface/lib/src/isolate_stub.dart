import 'package:moxlib/moxlib.dart';
import 'package:moxplatform_platform_interface/src/isolate.dart';

class StubDataSender extends AwaitableDataSender {
  StubDataSender() : super();

  @override
  Future<void> sendDataImpl(DataWrapper data) async {}
}

class StubIsolateHandler extends IsolateHandler {
  StubIsolateHandler() : _sender = StubDataSender();
  final StubDataSender _sender;

  @override
  Future<void> attach(
    Future<void> Function(Map<String, dynamic>? data) handleIsolateEvent,
  ) async {
    // ignore: avoid_print
    print('STUB ATTACHED!!!!!!');
  }

  @override
  Future<void> start(
    Future<void> Function() entrypoint,
    Future<void> Function(Map<String, dynamic>? data) handleUIEvent,
    Future<void> Function(Map<String, dynamic>? data) handleIsolateEvent,
  ) async {
    // ignore: avoid_print
    print('STUB STARTED!!!!!!');
  }

  @override
  Future<bool> isRunning() async => false;

  @override
  AwaitableDataSender getDataSender() => _sender;
}
