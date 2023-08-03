import 'package:flutter/services.dart';
import 'package:moxplatform_platform_interface/moxplatform_platform_interface.dart';

class AndroidCryptographyImplementation extends CryptographyImplementation {
  final MoxplatformApi _api = MoxplatformApi();

  @override
  Future<CryptographyResult?> encryptFile(
    String sourcePath,
    String destPath,
    Uint8List key,
    Uint8List iv,
    CipherAlgorithm algorithm,
    String hashSpec,
  ) async {
    return _api.encryptFile(
      sourcePath,
      destPath,
      key,
      iv,
      algorithm,
      hashSpec,
    );
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
    return _api.decryptFile(
      sourcePath,
      destPath,
      key,
      iv,
      algorithm,
      hashSpec,
    );
  }

  @override
  Future<Uint8List?> hashFile(String sourcePath, String hashSpec) async {
    return _api.hashFile(sourcePath, hashSpec);
  }
}
