package com.example.flutter_java_channels_demo;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.flutter_java_channels_demo.di.ChannelConfig;
import com.example.flutter_java_channels_demo.di.PluginEntryPoint;
import com.example.flutter_java_channels_demo.assignment1.domain.model.Todo;
import com.example.flutter_java_channels_demo.assignment1.domain.usecase.GetTodoUseCase;

import dagger.hilt.android.EntryPointAccessors;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MyFlutterPlugin implements
        FlutterPlugin,
        MethodChannel.MethodCallHandler,
        EventChannel.StreamHandler {
    private static final String TAG = "MyFlutterPlugin";

    private MethodChannel methodChannel;
    private EventChannel eventChannel;
    private EventChannel.EventSink eventSink;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private volatile CompletableFuture<String> pendingAnswer;

    private GetTodoUseCase getTodoUseCase;
    private ChannelConfig channelConfig;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        PluginEntryPoint entryPoint = EntryPointAccessors.fromApplication(
                binding.getApplicationContext(),
                PluginEntryPoint.class
        );
        getTodoUseCase = entryPoint.getTodoUseCase();
        channelConfig = entryPoint.getChannelConfig();

        methodChannel = new MethodChannel(
                binding.getBinaryMessenger(),
                channelConfig.getMethodChannelName()
        );
        methodChannel.setMethodCallHandler(this);

        eventChannel = new EventChannel(
                binding.getBinaryMessenger(),
                channelConfig.getEventChannelName()
        );
        eventChannel.setStreamHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        if (methodChannel != null) {
            methodChannel.setMethodCallHandler(null);
            methodChannel = null;
        }
        if (eventChannel != null) {
            eventChannel.setStreamHandler(null);
            eventChannel = null;
        }
        eventSink = null;
        channelConfig = null;
        backgroundExecutor.shutdownNow();
        scheduler.shutdownNow();
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "fetchApi":
                fetchApi(result);
                break;
            case "scheduleQuestion":
                Integer delaySeconds = call.argument("delaySeconds");
                int delay = delaySeconds != null ? delaySeconds : 5;
                scheduler.schedule(this::scheduledFunction, delay, TimeUnit.SECONDS);
                result.success("Question scheduled in " + delay + " second(s).");
                break;
            case "submitAnswer":
                submitAnswer(call, result);
                break;
            default:
                result.notImplemented();
        }
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        eventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {
        eventSink = null;
    }

    private void fetchApi(MethodChannel.Result result) {
        backgroundExecutor.execute(() -> {
            try {
                Todo todo = getTodoUseCase.execute(1);
                String text = "userId: " + todo.getUserId() + "\n"
                        + "id: " + todo.getId() + "\n"
                        + "title: " + todo.getTitle() + "\n"
                        + "completed: " + todo.isCompleted();
                mainHandler.post(() -> result.success(text));
            } catch (Exception e) {
                String message = e.getMessage() != null ? e.getMessage() : "Unknown error";
                mainHandler.post(() -> result.error("FETCH_FAILED", message, null));
            }
        });
    }

    private void submitAnswer(MethodCall call, MethodChannel.Result result) {
        String answer = call.argument("answer");
        if (answer == null) {
            result.error("INVALID_ARGUMENT", "Missing 'answer' argument", null);
            return;
        }

        CompletableFuture<String> future = pendingAnswer;
        if (future == null || future.isDone()) {
            result.error("NO_ACTIVE_QUESTION", "There is no active question.", null);
            return;
        }

        String normalized = answer.toUpperCase(Locale.US);
        if (!"YES".equals(normalized) && !"NO".equals(normalized)) {
            result.error("INVALID_ANSWER", "Answer must be YES or NO.", null);
            return;
        }

        future.complete(normalized);
        result.success("Answer submitted: " + normalized);
    }

    private void scheduledFunction() {
        String answer = getTheAnswer();
        Log.i(TAG, answer);
    }

    private String getTheAnswer() {
        CompletableFuture<String> future = new CompletableFuture<>();
        pendingAnswer = future;

        Map<String, Object> questionPayload = new HashMap<>();
        questionPayload.put("type", "question_asked");
        questionPayload.put("questionId", "scheduled-question");
        questionPayload.put("question", "Do you confirm this scheduled action?");
        questionPayload.put("timeLimitSeconds", 10);
        emitEvent(questionPayload);

        String answer = "OTHER";
        boolean timedOut = false;
        try {
            answer = future.get(10, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            timedOut = true;
            answer = "OTHER";
            future.complete("OTHER");
        } catch (Exception e) {
            answer = "OTHER";
        } finally {
            pendingAnswer = null;
        }

        Map<String, Object> closedPayload = new HashMap<>();
        closedPayload.put("type", "question_closed");
        closedPayload.put("questionId", "scheduled-question");
        closedPayload.put("answer", answer);
        closedPayload.put("timedOut", timedOut);
        emitEvent(closedPayload);

        return answer;
    }

    private void emitEvent(Map<String, Object> event) {
        mainHandler.post(() -> {
            if (eventSink != null) {
                eventSink.success(event);
            }
        });
    }
}
