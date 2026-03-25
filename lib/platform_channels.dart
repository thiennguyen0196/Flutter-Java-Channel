import 'package:flutter/services.dart';

const MethodChannel pluginMethodChannel = MethodChannel(
  'com.example.flutter_java_channels/plugin_method',
);

const EventChannel pluginEventChannel = EventChannel(
  'com.example.flutter_java_channels/plugin_event',
);
