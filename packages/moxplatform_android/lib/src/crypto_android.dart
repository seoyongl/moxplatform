import 'dart:typed_data';
import 'package:flutter/services.dart';
import 'package:moxplatform_platform_interface/moxplatform_platform_interface.dart';

class AndroidCryptographyImplementation extends CryptographyImplementation {
  final _methodChannel = const MethodChannel('me.polynom.moxplatform_android');

  @override
  Future<bool> encryptFile(String sourcePath, String destPath, Uint8List key, Uint8List iv, CipherAlgorithm algorithm) async {
    final result = await _methodChannel.invokeMethod<bool>('encryptFile', [
      sourcePath,
      destPath,
      key,
      iv,
      algorithm.toInt(),
    ]);
    return result ?? false;
  }

  @override
  Future<bool> decryptFile(String sourcePath, String destPath, Uint8List key, Uint8List iv, CipherAlgorithm algorithm) async {
    final result = await _methodChannel.invokeMethod<bool>('decryptFile', [
      sourcePath,
      destPath,
      key,
      iv,
      algorithm.toInt(),
    ]);
    return result ?? false;
  }
}
