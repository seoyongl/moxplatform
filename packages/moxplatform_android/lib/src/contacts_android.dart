import 'package:moxplatform_platform_interface/moxplatform_platform_interface.dart';
import 'package:moxplatform_platform_interface/src/api.g.dart';

class AndroidContactsImplementation extends ContactsImplementation {
  final MoxplatformApi _api = MoxplatformApi();

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

    return _api.recordSentMessage(
      name,
      jid,
      avatarPath,
      fallbackIcon,
    );
  }
}
