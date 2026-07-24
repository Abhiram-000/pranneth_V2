package com.rakshasetu.app.ui.calibration

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rakshasetu.app.R
import com.rakshasetu.app.data.entity.AppPreferences
import com.rakshasetu.app.data.repository.PreferencesRepository
import com.rakshasetu.app.databinding.ActivityCalibrationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CalibrationActivity : AppCompatActivity(), CalibrationHelper.CalibrationListener {

    @Inject lateinit var preferencesRepository: PreferencesRepository

    private lateinit var binding: ActivityCalibrationBinding
    private lateinit var calibrationHelper: CalibrationHelper
    private var currentStep = 0 // 0=instructions, 1=shake, 2=volume, 3=results

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalibrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calibrationHelper = CalibrationHelper(this, this)
        setupUI()
    }

    private fun setupUI() {
        showInstructions()

        binding.btnStartCalibration.setOnClickListener {
            when (currentStep) {
                0 -> startShakeCalibration()
                1 -> startVolumeCalibration()
                2 -> finishCalibration()
            }
        }

        binding.btnSkip.setOnClickListener {
            finish()
        }
    }

    private fun showInstructions() {
        currentStep = 0
        binding.tvTitle.text = "Guided Calibration"
        binding.tvSubtitle.text = "Practice your emergency gestures so we can tune the sensitivity for your phone"
        binding.tvInstruction.text = "We'll start with the SHAKE gesture.\n\nHold your phone the way you normally would (in hand, pocket, or bag), then shake it firmly when prompted."
        binding.btnStartCalibration.text = "Start Shake Calibration"
        binding.tvProgress.text = "Step 1 of 2"
        binding.progressIndicator.progress = 0
        binding.layoutResult.visibility = android.view.View.GONE
        binding.layoutInstructions.visibility = android.view.View.VISIBLE
    }

    private fun startShakeCalibration() {
        currentStep = 1
        binding.tvTitle.text = "Shake Calibration"
        binding.tvSubtitle.text = "Shake your phone firmly 3-5 times"
        binding.tvInstruction.text = "🔴 SHAKE NOW!\n\nShake your phone the way you would in an emergency.\nYou have 10 seconds."
        binding.btnStartCalibration.isEnabled = false
        binding.tvProgress.text = "Step 1 of 2 — In Progress"
        binding.progressIndicator.progress = 25

        vibratePattern(longArrayOf(0, 100, 50, 100, 50, 200))
        calibrationHelper.startCalibration()
    }

    private fun startVolumeCalibration() {
        currentStep = 2
        binding.tvTitle.text = "Volume Button Calibration"
        binding.tvSubtitle.text = "Press Volume Up + Volume Down together"
        binding.tvInstruction.text = "🔴 PRESS VOLUME BUTTONS!\n\nPress both Volume Up and Volume Down buttons at the same time, 3 times.\n\nThis tests the lock-screen trigger."
        binding.btnStartCalibration.text = "I'm Done"
        binding.progressIndicator.progress = 50

        vibratePattern(longArrayOf(0, 100, 50, 100))
    }

    private fun finishCalibration() {
        currentStep = 3
        binding.tvTitle.text = "Calibration Complete!"
        binding.tvSubtitle.text = "Your gestures have been tuned for your device"
        binding.layoutInstructions.visibility = android.view.View.GONE
        binding.layoutResult.visibility = android.view.View.VISIBLE
        binding.btnStartCalibration.text = "Save & Finish"
        binding.progressIndicator.progress = 100

        vibratePattern(longArrayOf(0, 200, 100, 200))
    }

    // CalibrationListener callbacks
    override fun onCalibrationStart() {
        binding.tvCalibrationStatus.text = "Detecting shakes..."
        binding.tvCalibrationStatus.setTextColor(getColor(R.color.sos_button))
    }

    override fun onShakeDetected(magnitude: Float, peakCount: Int) {
        binding.tvCalibrationStatus.text = "Shake detected! ($peakCount total, intensity: ${magnitude.toInt()})"
        binding.tvCalibrationStatus.setTextColor(getColor(R.color.status_active))
        vibratePattern(longArrayOf(0, 50))
    }

    override fun onCalibrationComplete(recommendedThreshold: Float, recommendedSensitivity: String) {
        val sensitivity = try {
            AppPreferences.Sensitivity.valueOf(recommendedSensitivity)
        } catch (_: Exception) {
            AppPreferences.Sensitivity.MEDIUM
        }

        preferencesRepository.update {
            it.copy(
                shakeThreshold = recommendedThreshold,
                triggerSensitivity = sensitivity
            )
        }

        binding.tvCalibrationStatus.text = "✅ Shake calibration successful!"
        binding.tvCalibrationStatus.setTextColor(getColor(R.color.status_active))
        binding.tvResultThreshold.text = "Recommended threshold: ${recommendedThreshold.toInt()}"
        binding.tvResultSensitivity.text = "Recommended sensitivity: $recommendedSensitivity"

        // Move to volume calibration
        lifecycleScope.launch {
            delay(1000)
            startVolumeCalibration()
        }
    }

    override fun onCalibrationError(error: String) {
        binding.tvCalibrationStatus.text = "⚠️ $error"
        binding.tvCalibrationStatus.setTextColor(getColor(R.color.warning))
        binding.btnStartCalibration.isEnabled = true
    }

    private fun vibratePattern(pattern: LongArray) {
        val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vm = getSystemService(VibratorManager::class.java)
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as? Vibrator
        }
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
    }

    override fun onDestroy() {
        calibrationHelper.stopCalibration()
        super.onDestroy()
    }
}
