class AdVisibilityTracker {
  bool _tracked = false;

  bool shouldTrack(
      double visibleFraction) {
    if (_tracked) return false;

    if (visibleFraction >= 0.5) {
      _tracked = true;
      return true;
    }

    return false;
  }
}