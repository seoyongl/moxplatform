import 'dart:io';
import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:file_picker/file_picker.dart';
import 'package:moxplatform/moxplatform.dart';
import 'package:moxplatform_platform_interface/moxplatform_platform_interface.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Moxplatform Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatelessWidget {
  const MyHomePage({super.key});

  Future<void> _cryptoTest() async {
    final result = await FilePicker.platform.pickFiles();
    if (result == null) {
      return;
    }

    final start = DateTime.now();
    final path = result.files.single.path;
    final enc = await MoxplatformPlugin.crypto.encryptFile(
      path!,
      '$path.enc',
      Uint8List.fromList(List.filled(32, 1)),
      Uint8List.fromList(List.filled(16, 2)),
      CipherAlgorithm.aes256CbcPkcs7,
      'SHA-256',
    );
    final end = DateTime.now();

    final diff = end.millisecondsSinceEpoch - start.millisecondsSinceEpoch;
    print('TIME: ${diff / 1000}s');
    print('DONE (${enc != null})');
    final lengthEnc = await File('$path.enc').length();
    final lengthOrig = await File(path).length();
    print('Encrypted file is $lengthEnc Bytes large (Orig $lengthOrig)');

    await MoxplatformPlugin.crypto.decryptFile(
      '$path.enc',
      '$path.dec',
      Uint8List.fromList(List.filled(32, 1)),
      Uint8List.fromList(List.filled(16, 2)),
      CipherAlgorithm.aes256CbcPkcs7,
      'SHA-256',
    );
    print('DONE');

    final lengthDec = await File('$path.dec').length();
    print('Decrypted file is $lengthDec Bytes large (Orig $lengthOrig)');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Moxplatform Demo'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
              onPressed: _cryptoTest,
              child: const Text('Test cryptography'),
            ),
            ElevatedButton(
              onPressed: () {
                MoxplatformPlugin.contacts.recordSentMessage('Hallo', 'Welt');
              },
              child: const Text('Test recordSentMessage (no fallback)'),
            ),
            ElevatedButton(
              onPressed: () {
                MoxplatformPlugin.contacts.recordSentMessage('Person', 'Person', fallbackIcon: FallbackIconType.person);
              },
              child: const Text('Test recordSentMessage (person fallback)'),
            ),
            ElevatedButton(
              onPressed: () {
                MoxplatformPlugin.contacts.recordSentMessage('Notes', 'Notes', fallbackIcon: FallbackIconType.notes);
              },
              child: const Text('Test recordSentMessage (notes fallback)'),
            ),
          ],
        ),
      ),
    );
  }
}
