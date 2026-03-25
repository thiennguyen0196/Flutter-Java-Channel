import 'package:flutter/material.dart';
import 'package:flutter_java_channel/l10n/app_localizations.dart';

import 'assignment_1_screen.dart';
import 'assignment_2_screen.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;
    return Scaffold(
      appBar: AppBar(title: Text(l10n.homeTitle)),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            FilledButton(
              onPressed: () {
                Navigator.of(context).push(
                  MaterialPageRoute<void>(
                    builder: (_) => const Assignment1Screen(),
                  ),
                );
              },
              child: Text(l10n.goToAssignment1),
            ),
            const SizedBox(height: 12),
            FilledButton(
              onPressed: () {
                Navigator.of(context).push(
                  MaterialPageRoute<void>(
                    builder: (_) => const Assignment2Screen(),
                  ),
                );
              },
              child: Text(l10n.goToAssignment2),
            ),
          ],
        ),
      ),
    );
  }
}
