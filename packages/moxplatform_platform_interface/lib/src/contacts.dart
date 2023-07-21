// The type of icon to use when no avatar path is provided.
enum FallbackIconType {
  none(-1),
  person(0),
  notes(1);

  const FallbackIconType(this.id);

  // The ID of the fallback icon.
  final int id;
}

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
