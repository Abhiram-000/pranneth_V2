package com.rakshasetu.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Restarts the shake detection service after device reboot.
 * This is critical for ensuring the app continues to work after restart.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d("BootReceiver", "Received broadcast: $action")

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED) {

            // Use shared prefs directly — Hilt may not be available in boot receiver
            val prefs = context.getSharedPreferences("rakshasetu_prefs", Context.MODE_PRIVATE)
            val onboardingComplete = prefs.getBoolean("is_onboarding_complete", false)
            val serviceEnabled = prefs.getBoolean("shake_detection_enabled", true)

            if (onboardingComplete && serviceEnabled) {
                Log.d("BootReceiver", "Restarting ShakeDetectionService after boot")
                ShakeDetectionService.start(context)
            } else {
                Log.d("BootReceiver", "Skipping restart: onboarding=$onboardingComplete, service=$serviceEnabled")
            }
            // Always (re)arm the watchdog after boot.
            com.rakshasetu.app.worker.WatchdogScheduler.schedule(context)
        }
    }
}
