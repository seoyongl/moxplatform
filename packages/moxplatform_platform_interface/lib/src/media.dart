/// Wrapper around platform-specific media scanning
abstract class MediaScannerImplementation {
  /// Let the platform-specific media scanner scan the file at [path].
  void scanFile(String path);
}
