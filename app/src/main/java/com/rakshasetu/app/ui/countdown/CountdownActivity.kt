package com.rakshasetu.app.ui.countdown

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import com.rakshasetu.app.util.LocationEnableHelper
import com.rakshasetu.app.util.OEMHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

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

        // Show trigger source
        binding.tvTriggerSource.text = when (triggerType) {
            "shake" -> "Triggered by: Shake Gesture"
            "volume" -> "Triggered by: Volume Buttons"
            "notification" -> "Triggered by: Notification"
            "airplane" -> "Triggered by: Airplane Mode Detected"
            else -> "Triggered by: Manual SOS"
        }

        // Check location before firing
        lifecycleScope.launch {
            ensureLocationEnabled()
            if (prefs.isSilentCountdown) {
                setupSilentCountdown()
            } else {
                setupVisibleCountdown()
            }
        }
    }

    private suspend fun ensureLocationEnabled() {
        if (!LocationEnableHelper.isLocationEnabled(this@CountdownActivity)) {
            // Try to enable location silently
            withContext(Dispatchers.IO) {
                LocationEnableHelper.requestLocationSettings(this@CountdownActivity)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupVisibleCountdown() {
        binding.root.visibility = View.VISIBLE
        binding.countdownContainer.visibility = View.VISIBLE

        binding.btnSafe.setOnClickListener {
            cancelAlert("user_cancel")
        }

        binding.btnSafe.setOnLongClickListener {
            val prefs = preferencesRepository.currentPrefs
            if (prefs.duressCode != null) {
                showDuressCancelDialog()
            }
            true
        }

        val duration = preferencesRepository.currentPrefs.countdownDurationSeconds
        binding.tvCountdown.text = duration.toString()
        startCountdown(duration)
    }

    private fun setupSilentCountdown() {
        binding.countdownContainer.visibility = View.GONE
        val duration = preferencesRepository.currentPrefs.countdownDurationSeconds
        startCountdown(duration)
    }

    private fun startCountdown(seconds: Int) {
        countdownJob = lifecycleScope.launch {
            for (i in seconds downTo 1) {
                if (!isFinishing) {
                    binding.tvCountdown?.text = i.toString()
                    AlertUtils.vibrateCountdownPattern(this@CountdownActivity)
                }
                delay(1000L)
            }
            fireAlert()
        }
    }

    private fun fireAlert() {
        lifecycleScope.launch {
            val batteryLevel = OEMHelper.getBatteryLevel(this@CountdownActivity)
            val hasData = OEMHelper.hasDataConnection(this@CountdownActivity)

            val alert = AlertLog(
                triggerType = triggerType,
                batteryLevel = batteryLevel,
                hasDataConnection = hasData
            )
            alertId = alertRepository.createAlert(alert)

            AlertDispatchService.dispatchAlert(this@CountdownActivity, alertId)

            AlertUtils.vibrateConfirmation(this@CountdownActivity)
            Toast.makeText(this@CountdownActivity, "🚨 Emergency alert sent!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun cancelAlert(method: String) {
        countdownJob?.cancel()
        AlertUtils.stopVibration(this)

        if (alertId != -1L) {
            AlertDispatchService.cancelAlert(this, alertId)
            Toast.makeText(this, "Alert cancelled", Toast.LENGTH_SHORT).show()
        } else {
            ShakeDetectionService.startCooldown(this)
            Toast.makeText(this, "Alert cancelled", Toast.LENGTH_SHORT).show()
        }

        AlertUtils.vibrateCancel(this)
        finish()
    }

    private fun showDuressCancelDialog() {
        val etCode = android.widget.EditText(this).apply {
            hint = "Enter duress code"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        AlertDialog.Builder(this)
            .setTitle("Duress Cancel")
            .setView(etCode)
            .setMessage("Enter your duress code to silently continue the alert while appearing to cancel.")
            .setPositiveButton("Confirm") { _, _ ->
                val code = etCode.text.toString()
                val prefs = preferencesRepository.currentPrefs
                if (prefs.duressCode != null && code == prefs.duressCode) {
                    // Duress cancel — alert continues silently
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
                } else {
                    Toast.makeText(this, "Incorrect code", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Go Back", null)
            .show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Prevent accidental back press during countdown
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
