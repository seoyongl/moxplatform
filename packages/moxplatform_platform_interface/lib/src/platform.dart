abstract class PlatformImplementation {
  /// Returns the path where persistent data should be stored.
  Future<String> getPersistentDataPath();

  /// Returns the path where cache data should be stored.
  Future<String> getCacheDataPath();
}
