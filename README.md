# سمپل نیتیو اندروید PushPanel (Android Studio)

اپ سمپل اتصال کتابخانه PushPanel به پروژه نیتیو اندروید.

کتابخانه: `ir.push-panel:push-sdk:1.8.3` از MavenCentral

## ساختار پروژه

```
├── app/
│   ├── google-services.json.example   # قالب فایل فایربیس (فایل واقعی git-ignored است)
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/pushpanel/test/
│       │   ├── SampleApplication.kt   # مقداردهی اولیه SDK
│       │   └── MainActivity.kt        # درخواست دسترسی + init + نمایش لاگ رویدادها
│       └── res/layout/activity_main.xml
├── build.gradle.kts                   # پلاگین‌ها (شامل google-services)
├── settings.gradle.kts
└── app/build.gradle.kts               # دیپندنسی ir.push-panel:push-sdk:1.8.3
```

## ۱. گریدل

`settings.gradle.kts` — داخل `dependencyResolutionManagement`:

```kotlin
repositories {
    google()
    mavenCentral()
}
```

`app/build.gradle.kts` — انتهای فایل:

```kotlin
dependencies {
    implementation("ir.push-panel:push-sdk:1.8.3")
}
```

## ۲. مقداردهی اولیه — کاتلین

```kotlin
PushPanel.init(this) // در Application.onCreate()؛ برای دیباگ: PushPanel.init(this, true)
```

## ۳. مین‌اکتیویتی — جاوا

```java
PushPanel.init(this); // در Application.onCreate()
```

## ۴. دسترسی‌ها و منیفست

`app/src/main/AndroidManifest.xml` — مستقیم داخل تگ `<manifest>` و قبل از `<application>`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

## ۵. دسترسی ران‌تایم نوتیفیکیشن (اندروید ۱۳ به بالا)

از اندروید ۱۳ (`TIRAMISU`) فقط داشتن دسترسی در مانیفست کافی نیست — باید در اکتیویتی (مثلاً `MainActivity.onCreate`) از کاربر گرفته شود، وگرنه نوتیفیکیشن نمایش داده نمی‌شود:

کاتلین:

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
    }
}
```

جاوا:

```java
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
    }
}
```

## ۶. فایربیس (برای دریافت واقعی پوش — اجباری)

بدون این مرحله `google-services.json` نادیده گرفته می‌شود، توکن FCM ساخته نمی‌شود و پوشی دریافت نمی‌کنی (بیلد موفق می‌شود ولی خبری از پوش نیست).

۱. فایل `google-services.json` را در پوشه `app/` بگذار؛ `package_name` داخل آن باید برابر `applicationId` برنامه (`com.pushpanel.test`) باشد.
(قالب خالی در `app/google-services.json.example` هست — فایل واقعی در گیت کامیت نمی‌شود.)

۲. در `build.gradle.kts` ریشه داخل بلاک `plugins` این خط را اضافه کن:

```kotlin
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
```

۳. در `app/build.gradle.kts` داخل بلاک `plugins` این خط را اضافه کن:

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}
```

۴. برای اطمینان بعد از بیلد، این فایل باید تولید شده باشد و شامل `google_app_id` باشد:

```
app/build/generated/res/google-services/debug/values/values.xml
```

## ۷. سرویس فایربیس اختصاصی (اختیاری — فقط اگر کتابخانه پوش دیگری هم داری)

FCM در هر اپ فقط به **یک** `FirebaseMessagingService` پیام تحویل می‌دهد. اگر کتابخانه دیگری هم سرویس خودش را دارد (یا خودت سرویس فایربیس داری)، باید سرویس داخلی SDK را حذف کنی و همه پیام‌ها را از سرویس خودت به هر کتابخانه فوروارد کنی — پیام‌هایی که مال پنل نیستند توسط SDK نادیده گرفته می‌شوند (مارکر `pushpanel=pushpanel`).

۱. در `app/src/main/AndroidManifest.xml` سرویس داخلی SDK را حذف کن (`xmlns:tools` را هم به تگ `manifest` اضافه کن):

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    <application ...>
        <!-- حذف سرویس داخلی SDK تا فقط سرویس خودمان پیام بگیرد -->
        <service
            android:name="ir.pushpanel.sdk.PushMessagingService"
            tools:node="remove" />
        <!-- سرویس خودمان -->
        <service
            android:name=".MyFirebaseService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
    </application>
</manifest>
```

۲. سرویس خودت همه پیام‌ها را فوروارد کند:

```kotlin
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ir.pushpanel.sdk.PushPanel

class MyFirebaseService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        PushPanel.forwardMessage(this, message) // فقط پیام‌های پنل هندل می‌شود، بقیه نادیده گرفته می‌شود
        // ... هندل پیام‌های خودت / کتابخانه دیگر ...
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        PushPanel.forwardToken(this, token)
    }
}
```

نکته: `PushPanel.init` در `SampleApplication` (بخش ۲) همچنان لازم است؛ فوروارد قبل از init نادیده گرفته می‌شود. اگر سرویس اختصاصی نداری، این بخش را رد کن — سرویس داخلی SDK به‌صورت پیش‌فرض کار می‌کند.

## ۸. بیلد

```powershell
./gradlew assembleDebug
```

## نکات

- برای دیدن لاگ‌های SDK در Logcat با تگ `PushPanel`، و لاگ‌های اپ سمپل با تگ `SampleApp` فیلتر کن.
