package com.example.call_recording_with_accessibility

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {
    override fun onCreate() {
        super.onCreate()
        Log.d("AppLog", "MyAccessibilityService onCreate")
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
    }

    override fun onInterrupt() {
        Log.d("AppLog", "MyAccessibilityService onInterrupt")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AppLog", "MyAccessibilityService onDestroy")
    }
}
