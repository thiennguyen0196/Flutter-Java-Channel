import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_java_channel/l10n/app_localizations.dart';

import 'platform_channels.dart';

class Assignment1Screen extends StatefulWidget {
  const Assignment1Screen({super.key});

  @override
  State<Assignment1Screen> createState() => _Assignment1ScreenState();
}

class _Assignment1ScreenState extends State<Assignment1Screen> {
  String _fetchApiResponse = '';

  Future<void> _fetchApi() async {
    final l10n = AppLocalizations.of(context)!;
    try {
      final response = await pluginMethodChannel.invokeMethod<String>(
        'fetchApi',
      );
      if (!mounted) return;
      setState(() {
        _fetchApiResponse = response ?? l10n.noResponseReceived;
      });
    } on PlatformException catch (e) {
      if (!mounted) return;
      setState(() {
        _fetchApiResponse = l10n.fetchError(e.message ?? '');
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;
    return Scaffold(
      appBar: AppBar(title: Text(l10n.assignment1Title)),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            FilledButton(onPressed: _fetchApi, child: Text(l10n.fetchApi)),
            const SizedBox(height: 12),
            Text(_fetchApiResponse),
          ],
        ),
      ),
    );
  }
}
