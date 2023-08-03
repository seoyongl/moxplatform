import 'package:moxplatform_platform_interface/src/api.g.dart';

// Wrapper around various contact APIs.
// ignore: one_member_abstracts
abstract class ContactsImplementation {
  Future<void> recordSentMessage(
    String name,
    String jid, {
    String? avatarPath,
    FallbackIconType fallbackIcon = FallbackIconType.none,
  });
}
