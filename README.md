# PushPanel Android Sample

Sample app for the PushPanel SDK: `ir.push-panel:push-sdk:1.7.2`.

It shows the minimal integration — init the SDK and listen for events.

## Setup

1. Create a Firebase project and download `google-services.json` for the
   package `com.pushpanel.test`.
   (A blank template lives at `app/google-services.json.example` —
   the real file is git-ignored on purpose.)
2. Copy it to `app/google-services.json`.
3. Sync & run:

```bash
./gradlew :app:assembleDebug
```

## What the sample shows

- `SampleApplication.kt` — one-line init:
  ```kotlin
  PushSdk.init(this, PushSdkConfig(debug = true))
  ```
- `MainActivity.kt`
  - Event listener (`PushSdk.addListener` → `onMessageReceived`,
    `onSilentMessage`, `onTokenRefreshed`)
  - Event log view.

## Dependency

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("ir.push-panel:push-sdk:1.7.2")
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
}
```

> Android 13+ needs the `POST_NOTIFICATIONS` runtime permission —
> the sample requests it on launch.

## Notes

- `PushSdkConfig.serverUrl` defaults to `https://push-panel.ir/api/v1`.
- Keep `autoRegisterToken = false` unless your panel app has the
  `token_delivery` permission; FCM topic subscriptions work regardless.
- Filter Logcat by `SampleApp`, `PushSDK` or `PushPanel` to see SDK logs.
