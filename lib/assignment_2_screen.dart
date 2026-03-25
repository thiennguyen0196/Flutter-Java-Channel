import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_java_channel/l10n/app_localizations.dart';

import 'platform_channels.dart';

class Assignment2Screen extends StatefulWidget {
  const Assignment2Screen({super.key});

  @override
  State<Assignment2Screen> createState() => _Assignment2ScreenState();
}

class _Assignment2ScreenState extends State<Assignment2Screen> {
  StreamSubscription<dynamic>? _eventSubscription;

  String _questionText = '';
  String _questionStatus = '';
  bool _isQuestionActive = false;

  @override
  void initState() {
    super.initState();
    _eventSubscription = pluginEventChannel.receiveBroadcastStream().listen(
      _onNativeEvent,
      onError: (Object error) {
        if (!mounted) return;
        setState(() {
          _questionStatus = AppLocalizations.of(context)!.eventError('$error');
          _isQuestionActive = false;
        });
      },
    );
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    if (_questionStatus.isEmpty) {
      _questionStatus = AppLocalizations.of(context)!.noActiveQuestion;
    }
  }

  @override
  void dispose() {
    _eventSubscription?.cancel();
    super.dispose();
  }

  Future<void> _scheduleQuestion() async {
    final l10n = AppLocalizations.of(context)!;
    try {
      final message = await pluginMethodChannel.invokeMethod<String>(
        'scheduleQuestion',
        {'delaySeconds': 3},
      );
      if (!mounted) return;
      setState(() {
        _questionStatus = message ?? l10n.questionScheduled;
      });
    } on PlatformException catch (e) {
      if (!mounted) return;
      setState(() {
        _questionStatus = l10n.scheduleError(e.message ?? '');
      });
    }
  }

  Future<void> _submitAnswer(String answer) async {
    final l10n = AppLocalizations.of(context)!;
    try {
      final message = await pluginMethodChannel.invokeMethod<String>(
        'submitAnswer',
        {'answer': answer},
      );
      if (!mounted) return;
      setState(() {
        _questionStatus = message ?? l10n.answerSubmitted;
        _isQuestionActive = false;
      });
    } on PlatformException catch (e) {
      if (!mounted) return;
      setState(() {
        _questionStatus = l10n.submitError(e.message ?? '');
      });
    }
  }

  void _onNativeEvent(dynamic event) {
    if (!mounted) return;
    if (event is! Map<dynamic, dynamic>) return;
    final l10n = AppLocalizations.of(context)!;

    final type = event['type'] as String?;
    if (type == 'question_asked') {
      setState(() {
        _questionText = event['question'] as String? ?? l10n.questionWasAsked;
        final limit = event['timeLimitSeconds'];
        _questionStatus = l10n.pleaseAnswerInSeconds('$limit');
        _isQuestionActive = true;
      });
      return;
    }

    if (type == 'question_closed') {
      final answer = event['answer'] as String? ?? 'OTHER';
      final timedOut = event['timedOut'] == true;
      setState(() {
        _isQuestionActive = false;
        _questionStatus = timedOut
            ? l10n.timeoutAnswer(answer)
            : l10n.questionClosedAnswer(answer);
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;
    return Scaffold(
      appBar: AppBar(title: Text(l10n.assignment2Title)),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            FilledButton(
              onPressed: _scheduleQuestion,
              child: Text(l10n.scheduleYesNoQuestion),
            ),
            const SizedBox(height: 12),
            Text(
              _questionText.isEmpty
                  ? l10n.questionWillAppearHere
                  : _questionText,
              style: const TextStyle(fontWeight: FontWeight.w600),
            ),
            const SizedBox(height: 8),
            Text(_questionStatus),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: FilledButton.tonal(
                    onPressed: _isQuestionActive
                        ? () => _submitAnswer('YES')
                        : null,
                    child: Text(l10n.yes),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: FilledButton.tonal(
                    onPressed: _isQuestionActive
                        ? () => _submitAnswer('NO')
                        : null,
                    child: Text(l10n.no),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
