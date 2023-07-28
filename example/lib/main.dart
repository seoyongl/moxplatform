import 'dart:io';
import 'dart:math';
import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:file_picker/file_picker.dart';
import 'package:moxplatform/moxplatform.dart';
import 'package:moxplatform_platform_interface/moxplatform_platform_interface.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:file_picker/file_picker.dart';

/// The id of the notification channel.
const channelId = "me.polynom.moxplatform.testing3";

void main() {
  runApp(const MyApp());
}

class Sender {
  const Sender(this.name, this.jid);

  final String name;

  final String jid;
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  MyAppState createState() => MyAppState();
}

class MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();

    initStateAsync();
  }

  Future<void> initStateAsync() async {
    await Permission.notification.request();

    await MoxplatformPlugin.notifications.createNotificationChannel("Test notification channel", channelId, false);

    MoxplatformPlugin.notifications.getEventStream().listen((event) {
      print('NotificationEvent(type: ${event.type}, jid: ${event.jid}, payload: ${event.payload})');
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Moxplatform Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatelessWidget {
  MyHomePage({super.key});

  /// List of "Message senders".
  final List<Sender> senders = const [
    Sender('Mash Kyrielight', 'mash@example.org'),
    Sender('Rio Tsukatsuki', 'rio@millenium'),
    Sender('Raiden Shogun', 'raiden@tevhat'),
  ];

  /// List of sent messages.
  List<NotificationMessage> messages = List<NotificationMessage>.empty(growable: true);

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
            ElevatedButton(
              onPressed: () async {
                final result = await FilePicker.platform.pickFiles(
                  type: FileType.image,
                );
                print('Picked file: ${result?.files.single.path}');

                // Create a new message.
                final senderIndex = Random().nextInt(senders.length);
                final time = DateTime.now().millisecondsSinceEpoch;
                messages.add(
                  NotificationMessage(
                    jid: senders[senderIndex].jid,
                    sender: senders[senderIndex].name,
                    content: NotificationMessageContent(
                      body: result != null ? null : 'Message #${messages.length}',
                      mime: 'image/jpeg',
                      path: result?.files.single.path,
                    ),
                    timestamp: time,
                  )
                );

                await Future<void>.delayed(const Duration(seconds: 4));
                await MoxplatformPlugin.notifications.showMessagingNotification(
                  MessagingNotification(
                    id: 2343,
                    title: 'Test conversation',
                    messages: messages,
                    channelId: channelId,
                    jid: 'testjid',
                  ),
                );
              },
              child: const Text('Show messaging notification'),
            ),
            ElevatedButton(
              onPressed: () async {
                print(await MoxplatformPlugin.platform.getPersistentDataPath());
              },
              child: const Text('Get data directory'),
            ),
            ElevatedButton(
              onPressed: () async {
                print(await MoxplatformPlugin.platform.getCacheDataPath());
              },
              child: const Text('Get cache directory'),
            ),
          ],
        ),
      ),
    );
  }
}
