package com.rakshasetu.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rakshasetu.app.data.repository.PreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Restarts the shake detection service after device reboot.
 * This is critical for ensuring the app continues to work after restart.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON" ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {

            val prefs = preferencesRepository.currentPrefs

            // Only restart if onboarding is complete and service was active
            if (prefs.isOnboardingComplete) {
                ShakeDetectionService.start(context)
            }
        }
    }
}
