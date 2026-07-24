package com.rakshasetu.app.data.entity

import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * App preferences stored in SharedPreferences via Hilt.
 */
data class AppPreferences(
    val isOnboardingComplete: Boolean = false,
    val triggerSensitivity: Sensitivity = Sensitivity.MEDIUM,
    val countdownDurationSeconds: Int = 7,
    val isSilentCountdown: Boolean = false,
    val shakeThreshold: Float = 12f,
    val shakeEventCount: Int = 3,
    val shakeWindowMs: Long = 2000L,
    val volumeComboEnabled: Boolean = true,
    val volumePressCount: Int = 2,
    val volumeWindowMs: Long = 300L,
    val cooldownDurationMs: Long = 60_000L,
    val smsTemplate: String = "EMERGENCY: I need help! My location: {location}. This is an automated alert from RakshaSetu. Please call me or check on me immediately.",
    val locationUpdateIntervalMs: Long = 120_000L,
    val locationUpdateDurationMs: Long = 1_800_000L,
    val emergencyNumber: String = "112",
    val duressCode: String? = null,
    val appLockEnabled: Boolean = false,
    val decoyModeEnabled: Boolean = false
) {
    enum class Sensitivity(val shakeThresholdMultiplier: Float) {
        LOW(1.5f),
        MEDIUM(1.0f),
        HIGH(0.6f)
    }

    companion object {
        private const val PREFS_NAME = "rakshasetu_prefs"

        fun fromSharedPreferences(prefs: SharedPreferences): AppPreferences {
            return AppPreferences(
                isOnboardingComplete = prefs.getBoolean("onboarding_complete", false),
                triggerSensitivity = try {
                    Sensitivity.valueOf(prefs.getString("sensitivity", "MEDIUM") ?: "MEDIUM")
                } catch (_: Exception) {
                    Sensitivity.MEDIUM
                },
                countdownDurationSeconds = prefs.getInt("countdown_duration", 7),
                isSilentCountdown = prefs.getBoolean("silent_countdown", false),
                shakeThreshold = prefs.getFloat("shake_threshold", 12f),
                shakeEventCount = prefs.getInt("shake_event_count", 3),
                shakeWindowMs = prefs.getLong("shake_window_ms", 2000L),
                volumeComboEnabled = prefs.getBoolean("volume_combo_enabled", true),
                volumePressCount = prefs.getInt("volume_press_count", 2),
                volumeWindowMs = prefs.getLong("volume_window_ms", 300L),
                cooldownDurationMs = prefs.getLong("cooldown_duration_ms", 60_000L),
                smsTemplate = prefs.getString("sms_template",
                    "EMERGENCY: I need help! My location: {location}. This is an automated alert from RakshaSetu. Please call me or check on me immediately.") ?: "",
                locationUpdateIntervalMs = prefs.getLong("location_update_interval", 120_000L),
                locationUpdateDurationMs = prefs.getLong("location_update_duration", 1_800_000L),
                emergencyNumber = prefs.getString("emergency_number", "112") ?: "112",
                duressCode = prefs.getString("duress_code", null),
                appLockEnabled = prefs.getBoolean("app_lock_enabled", false),
                decoyModeEnabled = prefs.getBoolean("decoy_mode_enabled", false)
            )
        }

        fun save(prefs: SharedPreferences, appPrefs: AppPreferences) {
            prefs.edit {
                putBoolean("onboarding_complete", appPrefs.isOnboardingComplete)
                putString("sensitivity", appPrefs.triggerSensitivity.name)
                putInt("countdown_duration", appPrefs.countdownDurationSeconds)
                putBoolean("silent_countdown", appPrefs.isSilentCountdown)
                putFloat("shake_threshold", appPrefs.shakeThreshold)
                putInt("shake_event_count", appPrefs.shakeEventCount)
                putLong("shake_window_ms", appPrefs.shakeWindowMs)
                putBoolean("volume_combo_enabled", appPrefs.volumeComboEnabled)
                putInt("volume_press_count", appPrefs.volumePressCount)
                putLong("volume_window_ms", appPrefs.volumeWindowMs)
                putLong("cooldown_duration_ms", appPrefs.cooldownDurationMs)
                putString("sms_template", appPrefs.smsTemplate)
                putLong("location_update_interval", appPrefs.locationUpdateIntervalMs)
                putLong("location_update_duration", appPrefs.locationUpdateDurationMs)
                putString("emergency_number", appPrefs.emergencyNumber)
                putString("duress_code", appPrefs.duressCode)
                putBoolean("app_lock_enabled", appPrefs.appLockEnabled)
                putBoolean("decoy_mode_enabled", appPrefs.decoyModeEnabled)
            }
        }
    }
}
