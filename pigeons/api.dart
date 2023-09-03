import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(
  PigeonOptions(
    dartOut: 'packages/moxplatform_platform_interface/lib/src/api.g.dart',
    //kotlinOut: 'packages/moxplatform_android/android/src/main/java/me/polynom/moxplatform_android/Notifications.g.kt',
    //kotlinOptions: KotlinOptions(
    //  package: 'me.polynom.moxplatform_android',
    //),
    javaOut: 'packages/moxplatform_android/android/src/main/java/me/polynom/moxplatform_android/Api.java',
    javaOptions: JavaOptions(
      package: 'me.polynom.moxplatform_android',
    ),
  ),
)
enum CipherAlgorithm {
  aes128GcmNoPadding,
  aes256GcmNoPadding,
  aes256CbcPkcs7;
}

class CryptographyResult {
  const CryptographyResult(this.plaintextHash, this.ciphertextHash);
  final Uint8List plaintextHash;
  final Uint8List ciphertextHash;
}

// The type of icon to use when no avatar path is provided.
enum FallbackIconType {
  none,
  person,
  notes;
}

@HostApi()
abstract class MoxplatformApi {
  /// Platform APIs
  String getPersistentDataPath();
  String getCacheDataPath();
  void openBatteryOptimisationSettings();
  bool isIgnoringBatteryOptimizations();

  /// Contacts APIs
  void recordSentMessage(String name, String jid, String? avatarPath, FallbackIconType fallbackIcon);

  /// Cryptography APIs
  @async CryptographyResult? encryptFile(String sourcePath, String destPath, Uint8List key, Uint8List iv, CipherAlgorithm algorithm, String hashSpec);
  @async CryptographyResult? decryptFile(String sourcePath, String destPath, Uint8List key, Uint8List iv, CipherAlgorithm algorithm, String hashSpec);
  @async Uint8List? hashFile(String sourcePath, String hashSpec);

  /// Media APIs
  bool generateVideoThumbnail(String src, String dest, int maxWidth);
}
