package com.rakshasetu.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.rakshasetu.app.domain.trigger.VolumeButtonDetector
import com.rakshasetu.app.ui.countdown.CountdownActivity

/**
 * Accessibility Service that captures volume key events even from the lock screen.
 * This is the primary reliable trigger mechanism alongside shake detection.
 *
 * Uses keyEvent interception to detect Volume Up+Down combo within a tight time window.
 * Works regardless of screen state (locked/unlocked).
 */
class VolumeKeyAccessibilityService : AccessibilityService(), VolumeButtonDetector.VolumeComboListener {

    private lateinit var volumeDetector: VolumeButtonDetector

    override fun onServiceConnected() {
        super.onServiceConnected()
        volumeDetector = VolumeButtonDetector(this, VolumeButtonDetector.VolumeConfig(
            requiredPresses = 2,
            windowMs = 300L,
            requireBothButtons = true
        ))
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used — we intercept key events instead
    }

    override fun onInterrupt() {
        // Service interrupted
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        // Capture volume key events
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
            event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volumeDetector.onKeyEvent(event.keyCode, event.action == KeyEvent.ACTION_DOWN)

            // Consume the event to prevent volume change during trigger
            // Only consume if we're actively looking for the combo
            // Let normal volume control work otherwise
            return false // Don't consume — let volume work normally
        }
        return super.onKeyEvent(event)
    }

    override fun onVolumeComboDetected() {
        // Launch countdown activity
        val intent = Intent(this, CountdownActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            putExtra(CountdownActivity.EXTRA_TRIGGER_TYPE, "volume")
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        /**
         * Checks if the accessibility service is enabled.
         */
        fun isEnabled(context: android.content.Context): Boolean {
            val serviceName = "${context.packageName}/${VolumeKeyAccessibilityService::class.java.canonicalName}"
            val enabledServices = android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            return enabledServices.contains(serviceName)
        }
    }
}
