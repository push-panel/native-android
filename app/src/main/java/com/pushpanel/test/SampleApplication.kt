package com.pushpanel.test

import android.app.Application
import ir.pushpanel.sdk.PushPanel

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Minimal setup: one line is enough.
        // serverUrl defaults to https://push-panel.ir/api/v1
        // debug = true prints SDK logs (Logcat tag: PushPanel).
        PushPanel.init(
            this,
            true)

    }

    companion object {
        private const val TAG = "SampleApp"
    }
}
