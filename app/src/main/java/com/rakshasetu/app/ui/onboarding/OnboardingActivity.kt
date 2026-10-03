package com.rakshasetu.app.ui.onboarding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.rakshasetu.app.R
import com.rakshasetu.app.data.repository.ContactRepository
import com.rakshasetu.app.data.repository.PreferencesRepository
import com.rakshasetu.app.util.SmsVerificationHelper
import com.rakshasetu.app.databinding.ActivityOnboardingBinding
import com.rakshasetu.app.ui.adapter.OnboardingPagerAdapter
import com.rakshasetu.app.ui.calibration.CalibrationActivity
import com.rakshasetu.app.ui.main.MainActivity
import com.rakshasetu.app.util.LocationEnableHelper
import com.rakshasetu.app.util.OEMHelper
import com.rakshasetu.app.util.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    @Inject lateinit var preferencesRepository: PreferencesRepository
    @Inject lateinit var contactRepository: ContactRepository

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var pagerAdapter: OnboardingPagerAdapter
    private var currentPage = 0

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            nextStep()
        } else {
            val denied = permissions.filter { !it.value }.keys
            val explanations = denied.map { PermissionHelper.getPermissionDisplayName(it) }
            Toast.makeText(this, "Required: ${explanations.joinToString(", ")}", Toast.LENGTH_LONG).show()
        }
    }

    private val backgroundLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        nextStep() // Continue regardless
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (preferencesRepository.currentPrefs.isOnboardingComplete) {
            // Skip directly to main — no visible redirect
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
            finish()
            return
        }

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPager()
        setupButtons()
        updatePage(0)
    }

    private fun setupPager() {
        pagerAdapter = OnboardingPagerAdapter()
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.isUserInputEnabled = false

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
                updatePage(position)
            }
        })
    }

    private fun setupButtons() {
        binding.btnNext.setOnClickListener {
            when (currentPage) {
                0 -> requestMainPermissions()
                1 -> {
                    if (!LocationEnableHelper.isLocationEnabled(this)) {
                        LocationEnableHelper.promptEnableLocation(this)
                    }
                    requestBackgroundLocation()
                }
                2 -> showBatteryOptimization()
                3 -> nextStep() // Contacts
                4 -> startActivity(Intent(this, CalibrationActivity::class.java))
                5 -> completeOnboarding()
            }
        }

        binding.btnSkip.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Skip Setup?")
                .setMessage("Some features may not work properly without completing setup. You can configure these later in Settings.")
                .setPositiveButton("Skip Anyway") { _, _ ->
                    completeOnboarding()
                }
                .setNegativeButton("Continue Setup", null)
                .show()
        }
    }

    private fun updatePage(position: Int) {
        binding.btnNext.text = when (position) {
            0 -> "Grant Permissions"
            1 -> "Enable Location"
            2 -> "Configure Battery"
            3 -> "Add Contacts"
            4 -> "Calibrate Gestures"
            5 -> "Start Using App"
            else -> "Next"
        }
        binding.btnSkip.visibility = if (position < 5) android.view.View.VISIBLE else android.view.View.GONE

        val progress = ((position + 1).toFloat() / 6 * 100).toInt()
        binding.progressIndicator.progress = progress
        binding.tvProgress.text = "Step ${position + 1} of 6"
    }

    private fun requestMainPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.VIBRATE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            permissionLauncher.launch(missing.toTypedArray())
        } else {
            nextStep()
        }
    }

    private fun requestBackgroundLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder(this)
                .setTitle("Background Location")
                .setMessage("Allow RakshaSetu to access your location even when the app is closed? This is needed to share your live location during an active emergency alert.")
                .setPositiveButton("Allow") { _, _ ->
                    backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
                .setNegativeButton("Skip") { _, _ ->
                    nextStep()
                }
                .show()
        } else {
            nextStep()
        }
    }

    private fun showBatteryOptimization() {
        val instructions = OEMHelper.getBatteryOptimizationInstructions(this)
        val stepsText = instructions.steps.joinToString("\n\n") { "• $it" }

        AlertDialog.Builder(this)
            .setTitle("${instructions.manufacturer} Battery Settings")
            .setMessage(
                "To ensure RakshaSetu works reliably on your ${instructions.manufacturer} device, " +
                "please disable battery optimization:\n\n$stepsText"
            )
            .setPositiveButton("Open Settings") { _, _ ->
                OEMHelper.openAppBatterySettings(this)
            }
            .setNegativeButton("I'll Do This Later") { _, _ ->
                nextStep()
            }
            .show()
    }

    private fun nextStep() {
        if (currentPage < 5) {
            binding.viewPager.setCurrentItem(currentPage + 1, true)
        }
    }

    private fun completeOnboarding() {
        preferencesRepository.completeOnboarding()
        // Non-blocking verification: send one clearly-labeled TEST SMS to the
        // first emergency contact so they recognize what a real alert looks like.
        kotlinx.coroutines.MainScope().launch {
            try {
                val contacts = contactRepository.getAllContactsList()
                if (contacts.isNotEmpty()) {
                    SmsVerificationHelper().sendTestSms(this@OnboardingActivity, contacts.first(), 777)
                    Toast.makeText(
                        this@OnboardingActivity,
                        "TEST SMS sent to ${contacts.first().name} — real alerts look the same",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (_: Exception) {
                // Never block finishing onboarding if the test SMS can't go out.
            }
        }
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        })
        finish()
    }
}
