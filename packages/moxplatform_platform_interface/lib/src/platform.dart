abstract class PlatformImplementation {
  /// Returns the path where persistent data should be stored.
  Future<String> getPersistentDataPath();

  /// Returns the path where cache data should be stored.
  Future<String> getCacheDataPath();

  /// Returns whether the app is battery-optimised (false) or
  /// excluded from battery savings (true).
  Future<bool> isIgnoringBatteryOptimizations();

  /// Opens the page for battery optimisations. If not supported on the
  /// platform, does nothing.
  Future<void> openBatteryOptimisationSettings();

  /// Attempt to generate a thumbnail for the video file at [src], scale it, while keeping the
  /// aspect ratio in tact to [width], and write it to [dest]. If we were successful, returns true.
  /// If no thumbnail was generated, returns false.
  Future<bool> generateVideoThumbnail(String src, String dest, int width);
}
