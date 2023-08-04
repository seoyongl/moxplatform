import 'package:moxplatform_platform_interface/src/api.g.dart';
import 'package:moxplatform_platform_interface/src/contacts.dart';

class StubContactsImplementation extends ContactsImplementation {
  @override
  Future<void> recordSentMessage(
    String name,
    String jid, {
    String? avatarPath,
    FallbackIconType fallbackIcon = FallbackIconType.none,
  }) async {}
}
