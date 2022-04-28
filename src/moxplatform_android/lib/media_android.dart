import "package:moxplatform_platform_interface/src/media.dart";
import "package:media_scanner/media_scanner.dart";

class AndroidMediaScannerImplementation extends MediaScannerImplementation {
  @override
  void scanFile(String path) {
    MediaScanner.loadMedia(path: path);
  }
}
