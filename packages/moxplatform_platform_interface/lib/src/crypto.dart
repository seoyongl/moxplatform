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

class CryptographyResult {

  const CryptographyResult(this.plaintextHash, this.ciphertextHash);
  final Uint8List plaintextHash;
  final Uint8List ciphertextHash;
}

/// Wrapper around platform-native cryptography APIs
abstract class CryptographyImplementation {
  /// Encrypt the file at [sourcePath] using [algorithm] and write the result back to
  /// [destPath]. [hashSpec] is the name of the Hash function to use, i.e. "SHA-256".
  /// Note that this function runs off-thread as to not block the UI thread.
  ///
  /// Resolves to true if the encryption was successful. Resolves to fale on failure.
  Future<CryptographyResult?> encryptFile(String sourcePath, String destPath, Uint8List key, Uint8List iv, CipherAlgorithm algorithm, String hashSpec);

  /// Decrypt the file at [sourcePath] using [algorithm] and write the result back to
  /// [destPath]. [hashSpec] is the name of the Hash function to use, i.e. "SHA-256".
  /// Note that this function runs off-thread as to not block the UI thread.
  /// 
  /// Resolves to true if the encryption was successful. Resolves to fale on failure.
  Future<CryptographyResult?> decryptFile(String sourcePath, String destPath, Uint8List key, Uint8List iv, CipherAlgorithm algorithm, String hashSpec);

  /// Hashes the file at [path] using the Hash function with name [hashSpec].
  /// Note that this function runs off-thread as to not block the UI thread.
  ///
  /// Returns the hash of the file.
  Future<Uint8List?> hashFile(String path, String hashSpec);
}
