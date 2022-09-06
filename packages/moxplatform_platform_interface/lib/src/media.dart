/// Wrapper around platform-specific media scanning
// ignore: one_member_abstracts
abstract class MediaScannerImplementation {
  /// Let the platform-specific media scanner scan the file at [path].
  void scanFile(String path);
}
