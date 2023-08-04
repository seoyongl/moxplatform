import 'dart:typed_data';
import 'package:moxplatform_platform_interface/src/api.g.dart';
import 'package:moxplatform_platform_interface/src/crypto.dart';

class StubCryptographyImplementation extends CryptographyImplementation {
  @override
  Future<CryptographyResult?> encryptFile(
    String sourcePath,
    String destPath,
    Uint8List key,
    Uint8List iv,
    CipherAlgorithm algorithm,
    String hashSpec,
  ) async {
    return null;
  }

  @override
  Future<CryptographyResult?> decryptFile(
    String sourcePath,
    String destPath,
    Uint8List key,
    Uint8List iv,
    CipherAlgorithm algorithm,
    String hashSpec,
  ) async {
    return null;
  }

  @override
  Future<Uint8List?> hashFile(String sourcePath, String hashSpec) async {
    return null;
  }
}
