package com.pushpanel.test

import android.app.Application
import android.util.Log
import ir.pushpanel.sdk.PushSdk
import ir.pushpanel.sdk.PushSdkConfig

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Minimal setup: one line is enough.
        // serverUrl defaults to https://push-panel.ir/api/v1
        // debug = true prints SDK logs (Logcat tag: PushSDK / PushPanel).
        PushSdk.init(
            this,
            PushSdkConfig(
                debug = true,
                // Set to true if your panel app has "token_delivery" permission
                // and you want the FCM token stored on the panel server.
                autoRegisterToken = false,
            )
        )

        Log.d(TAG, "PushSdk initialized: ${PushSdk.isInitialized()}")
    }

    companion object {
        private const val TAG = "SampleApp"
    }
}
