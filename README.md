# سمپل نیتیو اندروید PushPanel (Android Studio)

اپ سمپل اتصال کتابخانه PushPanel به پروژه نیتیو اندروید.

کتابخانه: `ir.push-panel:push-sdk:1.7.2` از MavenCentral

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
└── app/build.gradle.kts               # دیپندنسی ir.push-panel:push-sdk:1.7.2
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
    implementation("ir.push-panel:push-sdk:1.7.2")
}
```

## ۲. مقداردهی اولیه — کاتلین

`SampleApplication.kt` — یک خط کافیست:

```kotlin
PushSdk.init(this, PushSdkConfig(debug = true))
```

`MainActivity.kt` — درخواست دسترسی نوتیفیکیشن (اندروید ۱۳+) و گوش دادن به رویدادها:

```kotlin
package com.pushpanel.test

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.pushpanel.sdk.PushSdk

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestNotificationPermission()
        PushSdk.init(this)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }
    }
}
```

## ۳. مین‌اکتیویتی — جاوا

```java
package com.pushpanel.test;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ir.pushpanel.sdk.PushSdk;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestNotificationPermission();
        PushSdk.init(this);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }
}
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

- `PushSdkConfig.serverUrl` به‌صورت پیش‌فرض `https://push-panel.ir/api/v1` است.
- `autoRegisterToken = false` نگه دار، مگر اینکه اپ پنل تو دسترسی `token_delivery` داشته باشد.
- برای دیدن لاگ‌های SDK در Logcat با `SampleApp`، `PushSDK` یا `PushPanel` فیلتر کن.
