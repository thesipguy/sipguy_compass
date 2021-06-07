import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:sipguy_compass/sipguy_compass.dart';

void main() {
  const MethodChannel channel = MethodChannel('sipguy_compass');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await SipguyCompass.platformVersion, '42');
  });
}
