import 'package:media_scanner/media_scanner.dart';
import 'package:moxplatform_platform_interface/moxplatform_platform_interface.dart';

class AndroidMediaScannerImplementation extends MediaScannerImplementation {
  @override
  void scanFile(String path) {
    MediaScanner.loadMedia(path: path);
  }
}
