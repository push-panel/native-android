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
```

## ۴. دسترسی‌ها و منیفست

`app/src/main/AndroidManifest.xml` — بعد از `<manifest>`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

## ۵. فایربیس (برای دریافت واقعی پوش — اجباری)

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

## ۶. بیلد

```powershell
./gradlew assembleDebug
```

## نکات

- برای دیدن لاگ‌های SDK در Logcat با تگ `PushPanel`، و لاگ‌های اپ سمپل با تگ `SampleApp` فیلتر کن.
