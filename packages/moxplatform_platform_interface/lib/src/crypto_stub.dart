import 'dart:typed_data';
import 'package:moxplatform_platform_interface/src/crypto.dart';

class StubCryptographyImplementation extends CryptographyImplementation {
  @override
  Future<bool> encryptFile(String sourcePath, String destPath, Uint8List key, Uint8List iv, CipherAlgorithm algorithm) async {
    return false;
  }

  @override
  Future<bool> decryptFile(String sourcePath, String destPath, Uint8List key, Uint8List iv, CipherAlgorithm algorithm) async {
    return false;
  }
}
