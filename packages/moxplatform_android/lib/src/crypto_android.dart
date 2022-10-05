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

    // ignore: argument_type_not_assignable
    final result = Map<String, dynamic>.from(resultRaw);
    return CryptographyResult(
      result['plaintext_hash']! as Uint8List,
      result['ciphertext_hash']! as Uint8List,
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

    // ignore: argument_type_not_assignable
    final result = Map<String, dynamic>.from(resultRaw);
    return CryptographyResult(
      result['plaintext_hash']! as Uint8List,
      result['ciphertext_hash']! as Uint8List,
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
