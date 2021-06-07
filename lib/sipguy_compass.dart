import 'dart:async';

import 'package:flutter/services.dart';

class SipguyCompass {
  // static final SipguyCompass _instance = SipguyCompass._();

  // factory SipguyCompass() {
  //   return _instance;
  // }

  // SipguyCompass._();

  // static const MethodChannel _channel =
  // const MethodChannel('sipguy_compass/battery');
  static const EventChannel _compassChannel =
      const EventChannel('sipguy_compass/compass');
  int? batteryLevel; // = null;

  static Future<String?> get platformVersion async {
    // final String? version = await _channel.invokeMethod('getPlatformVersion');
    return 'version';
  }

  static Future<int?> get getBatteryLevel async {
    // String bateryLevel;
    try {
      // final int result = await _channel.invokeMethod('getBatteryLevel');
      return 0;
    } on PlatformException catch (e) {
      print(e);
    }
  }

  static Stream<CompassEvent>? get events {
    return _compassChannel
        .receiveBroadcastStream()
        .map((dynamic data) => CompassEvent.fromList(data.cast<double>()));
  }
}

class CompassEvent {
  // The heading, in degrees, of the device around its Z
  // axis, or where the top of the device is pointing.
  final double? heading;

  // The heading, in degrees, of the device around its X axis, or
  // where the back of the device is pointing.
  final double? headingForCameraMode;

  // The deviation error, in degrees, plus or minus from the heading.
  // NOTE: for iOS this is computed by the platform and is reliable. For
  // Android several values are hard-coded, and the true error could be more
  // or less than the value here.
  final double? accuracy;

  CompassEvent.fromList(List<double> data)
      : heading = data[0],
        headingForCameraMode = data[1],
        accuracy = data[2] == -1 ? null : data[2];

  @override
  String toString() {
    return 'heading: $heading\nheadingForCameraMode: $headingForCameraMode\naccuracy: $accuracy';
  }
}
