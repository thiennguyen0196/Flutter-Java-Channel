# Flutter Java Channel Demo

This app demonstrates communication between Flutter and Android native Java using `MethodChannel` and `EventChannel`.

## Prerequisites

- Flutter SDK installed and available in `PATH`
- Android SDK + Android emulator (or physical Android device)
- Android Studio (for IDE workflow)

## Run The App From Command Line

1. Open a terminal in the project root.
2. Install dependencies:

```bash
flutter pub get
```

3. Generate localization language files

```bash
flutter gen-l10n
```

4. Start an Android emulator (or connect a device).
5. Run the app:

```bash
flutter run
```

Optional: if you have multiple devices, select one:

```bash
flutter devices
flutter run -d <device_id>
```

## Run The App In Android Studio

1. Open Android Studio.
2. Select `Open` and choose this project folder:
   - `Flutter-Java-Channel`
3. Wait for Gradle/project sync to finish.
4. Select an Android device/emulator in the device selector.
5. Run the app using the green `Run` button (or `Shift + F10`).

You can also run `lib/main.dart` directly from the Flutter run configuration.

## Architecture Overview

The app has two main parts:

- Flutter UI layer (`lib/`):
  - Screens call native features through channels.
  - `platform_channels.dart` defines shared channel names used by Flutter.
- Android native layer (`android/app/src/main/java/...`):
  - `MainActivity` registers `MyFlutterPlugin`.
  - `MyFlutterPlugin` handles method calls and emits events.
  - Hilt modules provide dependencies (channel config, network, and use case classes).

### Data Flow (High Level)

- Assignment 1:
  - Flutter triggers a native method call.
  - Java plugin executes a use case (`GetTodoUseCase`) that reads data through repository and Retrofit API service.
  - Result is returned back to Flutter and rendered on screen.
- Assignment 2:
  - Flutter requests a scheduled question via method call.
  - Java plugin emits question lifecycle updates over `EventChannel`.
  - Flutter listens to events and sends user answers back through `MethodChannel`.

### Screen recorder

https://github.com/user-attachments/assets/b1d4b959-7eac-4866-9eb2-79ee8aeb3d43

