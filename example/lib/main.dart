import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:sipguy_compass/sipguy_compass.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  int _batteryLevel = 0;
  Stream<CompassEvent>? _compassEvents;

  @override
  void initState() {
    super.initState();
    initPlatformState();
    _compassEvents = SipguyCompass.events;
    _compassEvents!.listen((event) {
      print("Compass Listen in init $event");
    });
    print('initstate done');
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await SipguyCompass.platformVersion ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            Center(
              child: Text('Running on: $_platformVersion\n'),
            ),
            ElevatedButton(
                onPressed: () async {
                  // int? batteryLevel;
                  // batteryLevel = await SipguyCompass.getBatteryLevel;
                  // if (batteryLevel != null) {
                  //   setState(() {
                  //     _batteryLevel = batteryLevel!;
                  //   });
                  // }
                },
                child: Text("Get BatteryLevel")),
            //   Text(_batteryLevel.toString()),
            StreamBuilder(
                stream: SipguyCompass.events,
                builder: (context, snapshot) {
                  if (snapshot.hasData) {
                    return Text(snapshot.data.toString());
                  } else
                    return Center(
                      child: CircularProgressIndicator(),
                    );
                })
          ],
        ),
      ),
    );
  }
}
