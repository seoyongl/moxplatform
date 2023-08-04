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
}
