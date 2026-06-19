import 'package:flutter/services.dart';

class OsmosService {
  static const MethodChannel _channel = MethodChannel('osmos');

  Future<Map<String, dynamic>> loadAd() async {
    final result = await _channel.invokeMethod('loadAd');

    return Map<String, dynamic>.from(result);
  }

  Future<void> trackImpression({
    required String uclid,
    int position = 1,
  }) async {
    await _channel.invokeMethod('trackImpression', {
      'uclid': uclid,
      'position': position,
    });
  }

  Future<void> trackClick({
    required String uclid,
  }) async {
    await _channel.invokeMethod('trackClick', {
      'uclid': uclid,
    });
  }
}
