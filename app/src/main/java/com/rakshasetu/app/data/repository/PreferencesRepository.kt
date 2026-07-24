package com.rakshasetu.app.data.repository

import android.content.SharedPreferences
import com.rakshasetu.app.data.entity.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(
    private val prefs: SharedPreferences
) {
    private val _preferences = MutableStateFlow(AppPreferences.fromSharedPreferences(prefs))
    val preferences: StateFlow<AppPreferences> = _preferences.asStateFlow()

    val currentPrefs: AppPreferences
        get() = _preferences.value

    fun refresh() {
        _preferences.value = AppPreferences.fromSharedPreferences(prefs)
    }

    fun update(transform: (AppPreferences) -> AppPreferences) {
        val updated = transform(_preferences.value)
        _preferences.value = updated
        AppPreferences.save(prefs, updated)
    }

    fun completeOnboarding() {
        update { it.copy(isOnboardingComplete = true) }
    }

    fun setSensitivity(sensitivity: AppPreferences.Sensitivity) {
        update { it.copy(triggerSensitivity = sensitivity) }
    }

    fun setCountdownDuration(seconds: Int) {
        update { it.copy(countdownDurationSeconds = seconds) }
    }

    fun setSilentCountdown(enabled: Boolean) {
        update { it.copy(isSilentCountdown = enabled) }
    }

    fun setSmsTemplate(template: String) {
        update { it.copy(smsTemplate = template) }
    }

    fun setDuressCode(code: String?) {
        update { it.copy(duressCode = code) }
    }
}
