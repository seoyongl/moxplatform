import 'dart:io';
import 'dart:math';
import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:file_picker/file_picker.dart';
import 'package:moxplatform/moxplatform.dart';
import 'package:path/path.dart';
import 'package:permission_handler/permission_handler.dart';

/// The id of the notification channel.
const channelId = "me.polynom.moxplatform.testing3";
const otherChannelId = "me.polynom.moxplatform.testing4";

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

    await MoxplatformPlugin.notifications.createNotificationChannels(
      [
        NotificationChannel(
          id: channelId,
          title: "Test1",
          description: "Test notification channel",
          importance: NotificationChannelImportance.MIN,
          showBadge: true,
          vibration: false,
          enableLights: false,
        ),
        NotificationChannel(
          id: otherChannelId,
          title: "Test2",
          description: "Test notification channel for warnings",
          importance: NotificationChannelImportance.MIN,
          showBadge: true,
          vibration: false,
          enableLights: false,
        ),
      ],
    );
    await MoxplatformPlugin.notifications.setI18n(
      NotificationI18nData(
        reply: "答える",
        markAsRead: "読みた",
        you: "あなた",
      ),
    );

    MoxplatformPlugin.notifications.getEventStream().listen((event) {
      // ignore: avoid_print
      print(
        'NotificationEvent(type: ${event.type}, jid: ${event.jid}, payload: ${event.payload}, extras: ${event.extra})',
      );
    });
  }

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

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  MyHomePageState createState() => MyHomePageState();
}

class MyHomePageState extends State<MyHomePage> {
  /// List of "Message senders".
  final List<Sender> senders = const [
    Sender('Mash Kyrielight', 'mash@example.org'),
    Sender('Rio Tsukatsuki', 'rio@millenium'),
    Sender('Raiden Shogun', 'raiden@tevhat'),
  ];

  /// List of sent messages.
  List<NotificationMessage> messages =
      List<NotificationMessage>.empty(growable: true);

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
    // ignore: avoid_print
    print('TIME: ${diff / 1000}s');
    // ignore: avoid_print
    print('DONE (${enc != null})');
    final lengthEnc = await File('$path.enc').length();
    final lengthOrig = await File(path).length();
    // ignore: avoid_print
    print('Encrypted file is $lengthEnc Bytes large (Orig $lengthOrig)');

    await MoxplatformPlugin.crypto.decryptFile(
      '$path.enc',
      '$path.dec',
      Uint8List.fromList(List.filled(32, 1)),
      Uint8List.fromList(List.filled(16, 2)),
      CipherAlgorithm.aes256CbcPkcs7,
      'SHA-256',
    );
    // ignore: avoid_print
    print('DONE');

    final lengthDec = await File('$path.dec').length();
    // ignore: avoid_print
    print('Decrypted file is $lengthDec Bytes large (Orig $lengthOrig)');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Moxplatform Demo'),
      ),
      body: Center(
        child: ListView(
          children: [
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
                MoxplatformPlugin.contacts.recordSentMessage('Person', 'Person',
                    fallbackIcon: FallbackIconType.person);
              },
              child: const Text('Test recordSentMessage (person fallback)'),
            ),
            ElevatedButton(
              onPressed: () {
                MoxplatformPlugin.contacts.recordSentMessage('Notes', 'Notes',
                    fallbackIcon: FallbackIconType.notes);
              },
              child: const Text('Test recordSentMessage (notes fallback)'),
            ),
            ElevatedButton(
              onPressed: () async {
                final result = await FilePicker.platform.pickFiles(
                  type: FileType.image,
                );
                // ignore: avoid_print
                print('Picked file: ${result?.files.single.path}');

                // Create a new message.
                final senderIndex = Random().nextInt(senders.length);
                final time = DateTime.now().millisecondsSinceEpoch;
                messages.add(NotificationMessage(
                  jid: senders[senderIndex].jid,
                  sender: senders[senderIndex].name,
                  content: NotificationMessageContent(
                    body: result != null ? null : 'Message #${messages.length}',
                    mime: 'image/jpeg',
                    path: result?.files.single.path,
                  ),
                  timestamp: time,
                ));

                await Future<void>.delayed(const Duration(seconds: 4));
                await MoxplatformPlugin.notifications.showMessagingNotification(
                  MessagingNotification(
                    id: 2343,
                    title: 'Test conversation',
                    messages: messages,
                    channelId: channelId,
                    jid: 'testjid',
                    isGroupchat: true,
                    extra: {
                      'jid': 'testjid',
                      'avatarPath': 'lol',
                      'rio': 'cute',
                    },
                  ),
                );
              },
              child: const Text('Show messaging notification'),
            ),
            ElevatedButton(
              onPressed: () {
                MoxplatformPlugin.notifications.showNotification(
                  RegularNotification(
                    id: 4384,
                    title: 'Warning',
                    body: 'Something brokey',
                    channelId: otherChannelId,
                    icon: NotificationIcon.warning,
                  ),
                );
              },
              child: const Text('Show warning notification'),
            ),
            ElevatedButton(
              onPressed: () {
                MoxplatformPlugin.notifications.showNotification(
                  RegularNotification(
                    id: 4384,
                    title: 'Error',
                    body: "Lol, you're on your own",
                    channelId: otherChannelId,
                    icon: NotificationIcon.error,
                  ),
                );
              },
              child: const Text('Show error notification'),
            ),
            ElevatedButton(
              onPressed: () async {
                final result = await FilePicker.platform.pickFiles(
                  type: FileType.image,
                );
                if (result == null) return;

                MoxplatformPlugin.notifications
                    .setNotificationSelfAvatar(result.files.single.path!);
              },
              child: const Text('Set notification self-avatar'),
            ),
            ElevatedButton(
              onPressed: () async {
                // ignore: avoid_print
                print(await MoxplatformPlugin.platform.getPersistentDataPath());
              },
              child: const Text('Get data directory'),
            ),
            ElevatedButton(
              onPressed: () async {
                // ignore: avoid_print
                print(await MoxplatformPlugin.platform.getCacheDataPath());
              },
              child: const Text('Get cache directory'),
            ),
            ElevatedButton(
              onPressed: () async {
                // ignore: avoid_print
                print(await MoxplatformPlugin.platform
                    .isIgnoringBatteryOptimizations());
              },
              child: const Text('Is battery optimised?'),
            ),
            ElevatedButton(
              onPressed: () async {
                await MoxplatformPlugin.platform
                    .openBatteryOptimisationSettings();
              },
              child: const Text('Open battery optimisation page'),
            ),
            ElevatedButton(
              onPressed: () async {
                final result = await FilePicker.platform.pickFiles(
                  type: FileType.video,
                );
                if (result == null) return;

                final path = result.files.single.path!;
                final storagePath =
                    await MoxplatformPlugin.platform.getPersistentDataPath();
                final mediaPath = join(storagePath, 'media');
                if (!Directory(mediaPath).existsSync()) {
                  await Directory(mediaPath).create(recursive: true);
                }

                final internalPath = join(mediaPath, basename(path));
                // ignore: avoid_print
                print('Copying file');
                await File(path).copy(internalPath);

                // ignore: avoid_print
                print('Generating thumbnail');
                final thumbResult =
                    await MoxplatformPlugin.platform.generateVideoThumbnail(
                  internalPath,
                  '$internalPath.thumbnail.jpg',
                  720,
                );
                // ignore: avoid_print
                print('Success: $thumbResult');

                // ignore: use_build_context_synchronously
                await showDialog<void>(
                  context: context,
                  builder: (context) => Image.file(
                    File('$internalPath.thumbnail.jpg'),
                  ),
                );
              },
              child: const Text('Thumbnail'),
            )
          ],
        ),
      ),
    );
  }
}
