package com.pushpanel.test

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ir.pushpanel.sdk.PushPayload
import ir.pushpanel.sdk.PushSdk
import ir.pushpanel.sdk.PushSdkListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Sample screen for the PushPanel SDK (`ir.push-panel:push-sdk:1.7.2`).
 *
 * Shows the minimal integration:
 *  1. [SampleApplication] calls `PushSdk.init()` once.
 *  2. This activity listens for SDK events and shows them in a log.
 */
class MainActivity : AppCompatActivity(), PushSdkListener {

    private lateinit var statusText: TextView
    private lateinit var logText: TextView

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        logText = findViewById(R.id.logText)

        requestNotificationPermission()
        PushSdk.addListener(this)

        updateStatus()

        findViewById<Button>(R.id.clearLogButton).setOnClickListener {
            logText.text = ""
        }
    }

    override fun onDestroy() {
        PushSdk.removeListener(this)
        super.onDestroy()
    }

    // ---- PushSdkListener ---------------------------------------------------

    override fun onMessageReceived(payload: PushPayload) {
        appendLog("onMessageReceived: ${payload.title} / ${payload.body}")
    }

    override fun onSilentMessage(payload: PushPayload) {
        appendLog("onSilentMessage: extras=${payload.extras}")
    }

    override fun onTokenRefreshed(token: String) {
        appendLog("onTokenRefreshed")
    }

    // ---- Helpers -----------------------------------------------------------

    private fun updateStatus() {
        val initialized = PushSdk.isInitialized()
        statusText.text = if (initialized) {
            "SDK status: initialized ✓ (package=${packageName})"
        } else {
            "SDK status: NOT initialized — check SampleApplication"
        }
    }

    private fun appendLog(line: String) {
        runOnUiThread {
            val stamped = "[${timeFormat.format(Date())}] $line\n"
            logText.append(stamped)
            Log.d(TAG, line)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATIONS)
            }
        }
    }

    companion object {
        private const val TAG = "SampleApp"
        private const val REQUEST_NOTIFICATIONS = 100
    }
}
