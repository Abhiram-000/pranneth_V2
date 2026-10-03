package com.rakshasetu.app.ui.countdown

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.rakshasetu.app.R
import com.rakshasetu.app.databinding.ActivityCountdownBinding
import com.rakshasetu.app.domain.location.LocationTracker
import com.rakshasetu.app.service.AlertDispatchService
import com.rakshasetu.app.service.LocationTrackingService
import com.rakshasetu.app.ui.main.MainActivity
import com.rakshasetu.app.util.LocationEnableHelper
import com.rakshasetu.app.util.SmsVerificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Countdown screen before emergency alert is dispatched.
 *
 * KEY FIX: Pre-warms GPS during countdown so location is ready when alert fires.
 * Previously, location was only requested AFTER countdown finished — too late.
 */
@AndroidEntryPoint
class CountdownActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "CountdownActivity"
        const val EXTRA_TRIGGER_TYPE = "trigger_type"
        const val EXTRA_IS_SILENT = "is_silent"
        private const val PERMISSION_REQUEST_SMS = 2001
        private const val PREFS_NAME = "rakshasetu_prefs"

        fun start(context: Context, triggerType: String, isSilent: Boolean = false) {
            val intent = Intent(context, CountdownActivity::class.java).apply {
                putExtra(EXTRA_TRIGGER_TYPE, triggerType)
                putExtra(EXTRA_IS_SILENT, isSilent)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityCountdownBinding
    private var countdownJob: Job? = null
    private var triggerType: String = "unknown"
    private var isSilent: Boolean = false

    @Inject lateinit var locationTracker: LocationTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lock screen support — show over lock screen and wake device
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        binding = ActivityCountdownBinding.inflate(layoutInflater)
        setContentView(binding.root)

        triggerType = intent.getStringExtra(EXTRA_TRIGGER_TYPE) ?: "unknown"
        // Silent countdown comes from the intent extra OR the user's silent-countdown setting.
        val prefsSilent = preferences().isSilentCountdown
        isSilent = intent.getBooleanExtra(EXTRA_IS_SILENT, false) || prefsSilent
        val prefs = preferences()

        // Show trigger source
        binding.tvTriggerSource.text = when (triggerType) {
            "shake" -> "Triggered by: Shake Gesture"
            "volume" -> "Triggered by: Volume Buttons"
            "notification" -> "Triggered by: Notification"
            "airplane" -> "Triggered by: Airplane Mode Detected"
            else -> "Triggered by: Manual SOS"
        }

        // Wire up I'M SAFE / Cancel buttons
        setupCancelButton()

        // Handle back press via dispatcher (onBackPressed is deprecated)
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showCancelDialog()
            }
        })

        // CRITICAL: Pre-warm GPS during countdown so location is ready
        preWarmLocation()

        // Check location and permissions before firing
        lifecycleScope.launch {
            ensureLocationEnabled()
            ensureSmsPermission()
            if (isSilent) {
                setupSilentCountdown()
            } else {
                setupVisibleCountdown()
            }
        }
    }

    /**
     * Pre-warm GPS during countdown — this fixes the "location not sending" bug.
     * GPS takes time to get a fix (5-30s depending on conditions).
     * By requesting it during countdown, we have a fix ready when alert fires.
     */
    private fun preWarmLocation() {
        Log.d(TAG, "Pre-warming GPS during countdown")
        locationTracker.preWarmGps()

        // Also start a rapid polling loop to show location status
        lifecycleScope.launch {
            while (isActive) {
                val loc = locationTracker.lastKnownLocation.value
                val accuracy = locationTracker.currentAccuracy.value
                if (loc != null) {
                    binding.tvLocationStatus.text = "GPS locked: ±${accuracy?.toInt() ?: "?"}m"
                    binding.tvLocationStatus.setTextColor(getColor(R.color.success_green))
                } else {
                    binding.tvLocationStatus.text = "Acquiring GPS fix..."
                    binding.tvLocationStatus.setTextColor(getColor(R.color.warning_orange))
                }
                delay(1000L)
            }
        }
    }

    private suspend fun ensureLocationEnabled() {
        withContext(Dispatchers.IO) {
            if (!LocationEnableHelper.isLocationEnabled(this@CountdownActivity)) {
                withContext(Dispatchers.Main) {
                    AlertDialog.Builder(this@CountdownActivity)
                        .setTitle("Location Required")
                        .setMessage("Location services are required for emergency alerts. Please enable GPS.")
                        .setPositiveButton("Enable") { _, _ ->
                            lifecycleScope.launch {
                                LocationEnableHelper.requestLocationSettings(this@CountdownActivity)
                            }
                        }
                        .setNegativeButton("Cancel") { _, _ ->
                            finish()
                        }
                        .setCancelable(false)
                        .show()
                }
                // Wait and re-check
                delay(3000L)
            }
        }
    }

    private suspend fun ensureSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            withContext(Dispatchers.Main) {
                ActivityCompat.requestPermissions(
                    this@CountdownActivity,
                    arrayOf(Manifest.permission.SEND_SMS),
                    PERMISSION_REQUEST_SMS
                )
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupVisibleCountdown() {
        binding.layoutCountdownVisual.visibility = android.view.View.VISIBLE
        binding.layoutSilentVisual.visibility = android.view.View.GONE

        applyCountdownBehavior(CountdownPolicy.behavior(isSilent = false))

        val duration = preferences().countdownDurationSeconds.toLong()

        lifecycleScope.launch {
            var remaining = duration
            while (remaining > 0 && isActive) {
                binding.tvCountdownNumber.text = remaining.toString()

                // Pulse animation
                binding.tvCountdownNumber.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(200)
                    .withEndAction {
                        binding.tvCountdownNumber.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(200)
                            .start()
                    }
                    .start()

                // Update location status
                val loc = locationTracker.lastKnownLocation.value
                // Update location status text (tvCountdownNumber shows the count, location status is in tvLocationStatus)

                delay(1000L)
                remaining--
            }

            if (isActive) {
                fireAlert()
            }
        }
    }

    private fun setupSilentCountdown() {
        binding.layoutCountdownVisual.visibility = android.view.View.GONE
        binding.layoutSilentVisual.visibility = android.view.View.VISIBLE

        applyCountdownBehavior(CountdownPolicy.behavior(isSilent = true))

        val duration = preferences().silentCountdownDurationSeconds.toLong()

        lifecycleScope.launch {
            var remaining = duration
            while (remaining > 0 && isActive) {
                binding.tvSilentCountdownNumber.text = remaining.toString()

                // Update location status
                val loc = locationTracker.lastKnownLocation.value
                binding.tvSilentCountdownHint.text = if (loc != null) {
                    "Location ready: ±${loc.accuracy.toInt()}m"
                } else {
                    "Acquiring location... (${remaining}s remaining)"
                }

                delay(1000L)
                remaining--
            }

            if (isActive) {
                fireAlert()
            }
        }
    }

    /** Distinct vibration signature for a real countdown vs. a notification,
     *  and a dim screen for the silent path (so no glowing flash rings the
     *  phone in a pocket / alerts an observer). */
    @SuppressLint("MissingPermission")
    private fun applyCountdownBehavior(behavior: CountdownPolicy.Behavior) {
        if (behavior.vibrate) {
            val vibrator = if (android.os.Build.VERSION.SDK_INT >= 31) {
                val vm = getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
                vm.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(android.content.Context.VIBRATOR_SERVICE) as android.os.Vibrator
            }
            vibrator.vibrate(
                android.os.VibrationEffect.createWaveform(longArrayOf(0, 300, 150, 300, 150, 600), -1)
            )
        }
        if (behavior.dimScreen) {
            val attrs = window.attributes
            attrs.screenBrightness = 0.05f
            window.attributes = attrs
        }
    }

    private fun setupCancelButton() {
        // Visible countdown: I'M SAFE button
        binding.btnSafe.setOnClickListener {
            showCancelDialog()
        }
        // Long press for duress cancel (fake cancel but alert continues silently)
        binding.btnSafe.setOnLongClickListener {
            showDuressCancelDialog()
            true
        }

        // Silent countdown: CANCEL button
        binding.btnSilentCancel.setOnClickListener {
            showCancelDialog()
        }
        binding.btnSilentCancel.setOnLongClickListener {
            showDuressCancelDialog()
            true
        }
    }

    private fun showCancelDialog() {
        countdownJob?.cancel()
        AlertDialog.Builder(this)
            .setTitle("Cancel Alert?")
            .setMessage("Are you sure you want to cancel this emergency alert?")
            .setPositiveButton("Yes, Cancel") { _, _ ->
                finish()
            }
            .setNegativeButton("No, Keep") { dialog, _ ->
                // Restart countdown
                dialog.dismiss()
                lifecycleScope.launch {
                    if (isSilent) {
                        setupSilentCountdown()
                    } else {
                        setupVisibleCountdown()
                    }
                }
            }
            .setOnCancelListener {
                // If dialog dismissed by touching outside, also restart
                lifecycleScope.launch {
                    if (isSilent) {
                        setupSilentCountdown()
                    } else {
                        setupVisibleCountdown()
                    }
                }
            }
            .show()
    }

    private fun showDuressCancelDialog() {
        countdownJob?.cancel()
        val prefs = com.rakshasetu.app.util.SecurePrefs.secureOf(this)
        val duressCode = prefs.getString("duress_code", "") ?: ""

        if (duressCode.isEmpty()) {
            // No duress code set — just cancel normally
            showCancelDialog()
            return
        }

        val input = android.widget.EditText(this).apply {
            hint = "Enter PIN"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        // Duress cancel must be indistinguishable from a normal cancel dialog —
        // identical wording, identical toast, identical finish. The only
        // difference is the alert keeps running silently.
        AlertDialog.Builder(this)
            .setTitle("Cancel Alert?")
            .setMessage("Enter your PIN to cancel this emergency alert?")
            .setView(input)
            .setPositiveButton("Yes, Cancel") { _, _ ->
                if (input.text.toString() == duressCode) {
                    Toast.makeText(this, "Alert cancelled", Toast.LENGTH_SHORT).show()
                    // Duress path: alert keeps running, logged as duress.
                    AlertDispatchService.dispatchAlert(this, triggerType, isSilent = true, isDuress = true)
                    finish()
                } else {
                    Toast.makeText(this, "Wrong code", Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch {
                        if (isSilent) setupSilentCountdown() else setupVisibleCountdown()
                    }
                }
            }
            .setNegativeButton("No, Keep") { dialog, _ ->
                dialog.dismiss()
                lifecycleScope.launch {
                    if (isSilent) setupSilentCountdown() else setupVisibleCountdown()
                }
            }
            .show()
    }

    private fun fireAlert() {
        // Final location check — get best available
        val location = locationTracker.lastKnownLocation.value
        val accuracy = locationTracker.currentAccuracy.value

        Log.d(TAG, "Firing alert: trigger=$triggerType, location=${location?.latitude ?: "null"}, accuracy=${accuracy ?: "null"}m")

        // Dispatch the alert
        AlertDispatchService.dispatchAlert(
            context = this,
            triggerType = triggerType,
            isSilent = isSilent
        )

        // Show confirmation and navigate
        lifecycleScope.launch {
            delay(500L)
            startActivity(Intent(this@CountdownActivity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
            finish()
        }
    }

    private fun preferences() = com.rakshasetu.app.util.SecurePrefs.secureOf(this).let {
        object {
            val countdownDurationSeconds: Int get() = it.getInt("countdown_duration", 10)
            val silentCountdownDurationSeconds: Int get() = it.getInt("silent_countdown_duration", 30)
            val isSilentCountdown: Boolean get() = it.getBoolean("is_silent_countdown", false)
        }
    }
}
