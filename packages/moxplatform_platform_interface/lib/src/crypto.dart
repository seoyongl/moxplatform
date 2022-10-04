import 'dart:typed_data';

enum CipherAlgorithm {
  aes128GcmNoPadding,
  aes256GcmNoPadding,
  aes256CbcPkcs7,
}

extension CipherAlgorithmToIntExtension on CipherAlgorithm {
  int toInt() {
    switch (this) {
      case CipherAlgorithm.aes128GcmNoPadding: return 0;
      case CipherAlgorithm.aes256GcmNoPadding: return 1;
      case CipherAlgorithm.aes256CbcPkcs7: return 2;
    }
  }
}

/// Wrapper around platform-native cryptography APIs
abstract class CryptographyImplementation {
  /// Encrypt the file at [sourcePath] using [algorithm] and write the result back to
  /// [destPath]. Note that this function runs off-thread as to not block the UI thread.
  ///
  /// Resolves to true if the encryption was successful. Resolves to fale on failure.
  Future<bool> encryptFile(String sourcePath, String destPath, Uint8List key, Uint8List iv, CipherAlgorithm algorithm);

  /// Decrypt the file at [sourcePath] using [algorithm] and write the result back to
  /// [destPath]. Note that this function runs off-thread as to not block the UI thread.
  /// 
  /// Resolves to true if the encryption was successful. Resolves to fale on failure.
  Future<bool> decryptFile(String sourcePath, String destPath, Uint8List key, Uint8List iv, CipherAlgorithm algorithm);
}
