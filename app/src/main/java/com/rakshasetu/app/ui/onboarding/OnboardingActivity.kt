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
import com.rakshasetu.app.data.repository.PreferencesRepository
import com.rakshasetu.app.databinding.ActivityOnboardingBinding
import com.rakshasetu.app.ui.adapter.OnboardingPagerAdapter
import com.rakshasetu.app.ui.main.MainActivity
import com.rakshasetu.app.util.OEMHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    @Inject lateinit var preferencesRepository: PreferencesRepository

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var pagerAdapter: OnboardingPagerAdapter

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            nextStep()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Skip onboarding if already complete
        if (preferencesRepository.currentPrefs.isOnboardingComplete) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPager()
        setupButtons()
    }

    private fun setupPager() {
        pagerAdapter = OnboardingPagerAdapter()
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.isUserInputEnabled = false // Disable swipe — use buttons only

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateButtons(position)
            }
        })
    }

    private fun setupButtons() {
        binding.btnNext.setOnClickListener {
            val currentPos = binding.viewPager.currentItem
            when (currentPos) {
                0 -> requestPermissions()
                1 -> showBatteryOptimization()
                2 -> nextStep() // Skip to main — contacts will be added from there
                3 -> completeOnboarding()
            }
        }

        binding.btnSkip.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Skip Onboarding?")
                .setMessage(
                    "Some features may not work properly without completing setup. " +
                    "You can configure these later in Settings."
                )
                .setPositiveButton("Skip Anyway") { _, _ ->
                    completeOnboarding()
                }
                .setNegativeButton("Continue Setup", null)
                .show()
        }
    }

    private fun updateButtons(position: Int) {
        binding.btnNext.text = when (position) {
            0 -> "Grant Permissions"
            1 -> "Configure Battery"
            2 -> "Add Contacts"
            3 -> "Start Using App"
            else -> "Next"
        }
        binding.btnSkip.visibility = if (position < 3) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }
        binding.viewPager.setCurrentItem(position, true)
    }

    private fun requestPermissions() {
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
        val currentPos = binding.viewPager.currentItem
        if (currentPos < 3) {
            binding.viewPager.setCurrentItem(currentPos + 1, true)
        }
    }

    private fun completeOnboarding() {
        preferencesRepository.completeOnboarding()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
