import 'package:flutter/services.dart';
import 'package:moxplatform_platform_interface/moxplatform_platform_interface.dart';

class AndroidContactsImplementation extends ContactsImplementation {
  final _methodChannel = const MethodChannel('me.polynom.moxplatform_android');

  @override
  Future<void> recordSentMessage(
    String name,
    String jid, {
    String? avatarPath,
    FallbackIconType fallbackIcon = FallbackIconType.none,
  }) async {
    // Ensure we always have an icon
    if (avatarPath != null) {
      assert(
        fallbackIcon != FallbackIconType.none,
        'If no avatar is specified, then a fallbackIcon must be set',
      );
    }

    await _methodChannel.invokeMethod<void>(
      'recordSentMessage',
      [
        name,
        jid,
        avatarPath,
        fallbackIcon.id,
      ],
    );
  }
}
