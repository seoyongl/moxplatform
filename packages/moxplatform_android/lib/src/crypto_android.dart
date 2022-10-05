import 'dart:typed_data';
import 'package:flutter/services.dart';
import 'package:moxplatform_platform_interface/moxplatform_platform_interface.dart';

class AndroidCryptographyImplementation extends CryptographyImplementation {
  final _methodChannel = const MethodChannel('me.polynom.moxplatform_android');

  @override
  Future<CryptographyResult?> encryptFile(String sourcePath, String destPath, Uint8List key, Uint8List iv, CipherAlgorithm algorithm, String hashSpec) async {
    final dynamic resultRaw = await _methodChannel.invokeMethod<dynamic>('encryptFile', [
      sourcePath,
      destPath,
      key,
      iv,
      algorithm.toInt(),
      hashSpec,
    ]);
    if (resultRaw == null) return null;

    final result = Map<String, Uint8List>.from(resultRaw as Map<String, dynamic>);
    return CryptographyResult(
      result['plaintext_hash']!,
      result['ciphertext_hash']!,
    );
  }

  @override
  Future<CryptographyResult?> decryptFile(String sourcePath, String destPath, Uint8List key, Uint8List iv, CipherAlgorithm algorithm, String hashSpec) async {
    final dynamic resultRaw = await _methodChannel.invokeMethod<dynamic>('decryptFile', [
      sourcePath,
      destPath,
      key,
      iv,
      algorithm.toInt(),
      hashSpec,
    ]);
    if (resultRaw == null) return null;

    final result = Map<String, Uint8List>.from(resultRaw as Map<String, dynamic>);
    return CryptographyResult(
      result['plaintext_hash']!,
      result['ciphertext_hash']!,
    );
  }

  @override
  Future<Uint8List?> hashFile(String path, String hashSpec) async {
    final dynamic resultsRaw = await _methodChannel.invokeMethod<dynamic>('hashFile', [
      path,
      hashSpec,
    ]);

    if (resultsRaw == null) return null;

    return resultsRaw as Uint8List;
  }
}
