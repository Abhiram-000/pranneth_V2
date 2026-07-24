package com.rakshasetu.app.ui.countdown

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rakshasetu.app.R
import com.rakshasetu.app.data.entity.AlertLog
import com.rakshasetu.app.data.repository.AlertRepository
import com.rakshasetu.app.data.repository.PreferencesRepository
import com.rakshasetu.app.databinding.ActivityCountdownBinding
import com.rakshasetu.app.service.AlertDispatchService
import com.rakshasetu.app.service.ShakeDetectionService
import com.rakshasetu.app.util.AlertUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Full-screen countdown activity that appears when a trigger is detected.
 * Shows a high-contrast countdown with a large "I'm Safe / Cancel" button.
 *
 * Two modes:
 * - Visible mode: Full-screen countdown with timer
 * - Silent mode: No screen change, only subtle vibration
 *
 * This is the critical safety net that converts "sensor thinks something happened"
 * into "confirmed emergency."
 */
@AndroidEntryPoint
class CountdownActivity : AppCompatActivity() {

    @Inject lateinit var alertRepository: AlertRepository
    @Inject lateinit var preferencesRepository: PreferencesRepository

    private lateinit var binding: ActivityCountdownBinding
    private var countdownJob: Job? = null
    private var triggerType: String = "unknown"
    private var alertId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show over lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }

        binding = ActivityCountdownBinding.inflate(layoutInflater)
        setContentView(binding.root)

        triggerType = intent.getStringExtra(EXTRA_TRIGGER_TYPE) ?: "unknown"
        val prefs = preferencesRepository.currentPrefs

        if (prefs.isSilentCountdown) {
            // Silent mode — don't show UI, just vibrate subtly
            setupSilentCountdown()
        } else {
            // Visible mode — show full-screen countdown
            setupVisibleCountdown()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupVisibleCountdown() {
        binding.root.visibility = View.VISIBLE

        // Large "I'm Safe" button
        binding.btnSafe.setOnClickListener {
            cancelAlert("user_cancel")
        }

        // Duress cancel — if user enters duress code, alert appears cancelled but continues
        binding.btnSafe.setOnLongClickListener {
            val prefs = preferencesRepository.currentPrefs
            if (prefs.duressCode != null) {
                showDuressCancelDialog()
            }
            true
        }

        // Start countdown
        val duration = preferencesRepository.currentPrefs.countdownDurationSeconds
        binding.tvCountdown.text = duration.toString()
        startCountdown(duration)
    }

    private fun setupSilentCountdown() {
        binding.root.visibility = View.GONE

        val duration = preferencesRepository.currentPrefs.countdownDurationSeconds
        startCountdown(duration)
    }

    private fun startCountdown(seconds: Int) {
        countdownJob = lifecycleScope.launch {
            for (i in seconds downTo 1) {
                binding.tvCountdown?.text = i.toString()
                AlertUtils.vibrateCountdownPattern(this@CountdownActivity)
                delay(1000L)
            }

            // Countdown finished — fire the alert
            fireAlert()
        }
    }

    private fun fireAlert() {
        lifecycleScope.launch {
            // Create alert log
            val alert = AlertLog(
                triggerType = triggerType,
                batteryLevel = com.rakshasetu.app.util.OEMHelper.getBatteryLevel(this@CountdownActivity),
                hasDataConnection = com.rakshasetu.app.util.OEMHelper.hasDataConnection(this@CountdownActivity)
            )
            alertId = alertRepository.createAlert(alert)

            // Dispatch the alert
            AlertDispatchService.dispatchAlert(this@CountdownActivity, alertId)

            Toast.makeText(this@CountdownActivity, "🚨 Emergency alert sent!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun cancelAlert(method: String) {
        countdownJob?.cancel()
        AlertUtils.stopVibration(this)

        if (alertId != -1L) {
            // Alert already fired — need to cancel it
            AlertDispatchService.cancelAlert(this, alertId)
            Toast.makeText(this, "Alert cancelled", Toast.LENGTH_SHORT).show()
        } else {
            // Countdown was cancelled before firing
            ShakeDetectionService.startCooldown(this)
            Toast.makeText(this, "Alert cancelled", Toast.LENGTH_SHORT).show()
        }

        AlertUtils.vibrateCancel(this)
        finish()
    }

    private fun showDuressCancelDialog() {
        val prefs = preferencesRepository.currentPrefs
        AlertDialog.Builder(this)
            .setTitle("Enter Duress Code")
            .setMessage("Enter your duress code to cancel (alert will continue silently)")
            .setPositiveButton("Cancel Alert") { _, _ ->
                // The duress code would be verified here
                // For now, just fire the alert silently
                lifecycleScope.launch {
                    val alert = AlertLog(
                        triggerType = triggerType,
                        isDuress = true
                    )
                    alertId = alertRepository.createAlert(alert)
                    AlertDispatchService.dispatchAlert(this@CountdownActivity, alertId)
                    Toast.makeText(this@CountdownActivity, "Alert cancelled", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("Go Back", null)
            .show()
    }

    override fun onBackPressed() {
        // Prevent accidental back press during countdown
        // User must explicitly tap "I'm Safe" to cancel
    }

    override fun onDestroy() {
        countdownJob?.cancel()
        AlertUtils.stopVibration(this)
        super.onDestroy()
    }

    companion object {
        const val EXTRA_TRIGGER_TYPE = "trigger_type"
    }
}
