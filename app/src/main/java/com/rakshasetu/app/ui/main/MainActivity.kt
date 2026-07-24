package com.rakshasetu.app.ui.main

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
import androidx.lifecycle.lifecycleScope
import com.rakshasetu.app.R
import com.rakshasetu.app.data.repository.ContactRepository
import com.rakshasetu.app.data.repository.PreferencesRepository
import com.rakshasetu.app.databinding.ActivityMainBinding
import com.rakshasetu.app.service.ShakeDetectionService
import com.rakshasetu.app.service.VolumeKeyAccessibilityService
import com.rakshasetu.app.ui.alertlog.AlertLogActivity
import com.rakshasetu.app.ui.calibration.CalibrationActivity
import com.rakshasetu.app.ui.contacts.ContactListActivity
import com.rakshasetu.app.ui.countdown.CountdownActivity
import com.rakshasetu.app.ui.settings.SettingsActivity
import com.rakshasetu.app.util.LocationEnableHelper
import com.rakshasetu.app.util.OEMHelper
import com.rakshasetu.app.util.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var contactRepository: ContactRepository
    @Inject lateinit var preferencesRepository: PreferencesRepository

    private lateinit var binding: ActivityMainBinding

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            checkLocationAndStart()
        } else {
            val denied = permissions.filter { !it.value }.keys
            val explanations = denied.map { "${PermissionHelper.getPermissionDisplayName(it)}: ${PermissionHelper.getPermissionExplanation(it)}" }
            Toast.makeText(this, "Missing permissions:\n${explanations.joinToString("\n")}", Toast.LENGTH_LONG).show()
        }
    }

    private val backgroundLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(this, "Background location enabled — alerts will track you continuously", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Background location denied — location updates may be less accurate", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeContactCount()
    }

    private fun setupUI() {
        // SOS Button
        binding.btnSos.setOnClickListener {
            val intent = Intent(this, CountdownActivity::class.java).apply {
                putExtra(CountdownActivity.EXTRA_TRIGGER_TYPE, "manual")
            }
            startActivity(intent)
        }

        // Manage Contacts
        binding.btnManageContacts.setOnClickListener {
            startActivity(Intent(this, ContactListActivity::class.java))
        }

        // Settings
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Calibration
        binding.btnCalibration.setOnClickListener {
            startActivity(Intent(this, CalibrationActivity::class.java))
        }

        // Alert History
        binding.btnAlertHistory.setOnClickListener {
            startActivity(Intent(this, AlertLogActivity::class.java))
        }

        // Location status
        binding.tvLocationStatus.setOnClickListener {
            LocationEnableHelper.promptEnableLocation(this)
        }

        // Emergency call 112
        binding.btnCall112.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Call 112 (Emergency)")
                .setMessage("This will place an emergency call to 112 (India's ERSS). Only do this in a real emergency.")
                .setPositiveButton("Call Now") { _, _ ->
                    try {
                        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:112"))
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Cannot make call: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun observeContactCount() {
        lifecycleScope.launch {
            contactRepository.getContactCount().collectLatest { count ->
                binding.tvContactCount.text = "$count emergency contact(s) configured"
                binding.btnSos.isEnabled = count > 0

                if (count == 0) {
                    binding.tvWarning.text = "⚠️ Add at least one emergency contact to enable SOS"
                    binding.tvWarning.visibility = android.view.View.VISIBLE
                    binding.layoutWarning.visibility = android.view.View.VISIBLE
                } else {
                    binding.tvWarning.visibility = android.view.View.GONE
                    binding.layoutWarning.visibility = android.view.View.GONE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissionsAndStart()
        updateServiceStatus()
        updateAccessibilityStatus()
        updateLocationStatus()
    }

    private fun checkPermissionsAndStart() {
        val status = PermissionHelper.checkAllPermissions(this)
        if (status.allGranted) {
            checkLocationAndStart()
        } else {
            permissionLauncher.launch(status.missing.toTypedArray())
        }

        // Request background location separately (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            !PermissionHelper.hasBackgroundLocationPermission(this)) {
            backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    private fun checkLocationAndStart() {
        if (!LocationEnableHelper.isLocationEnabled(this)) {
            binding.tvLocationStatus.text = "⚠️ Location services OFF — tap to enable"
            binding.tvLocationStatus.setTextColor(ContextCompat.getColor(this, R.color.warning))
            binding.tvLocationStatus.setOnClickListener {
                LocationEnableHelper.promptEnableLocation(this)
            }

            AlertDialog.Builder(this)
                .setTitle("Location Services Disabled")
                .setMessage(
                    "RakshaSetu needs location services to share your GPS coordinates during emergencies.\n\n" +
                    "Current status: ${LocationEnableHelper.getLocationStatusDescription(this)}\n\n" +
                    "Please enable location services."
                )
                .setPositiveButton("Open Settings") { _, _ ->
                    LocationEnableHelper.promptEnableLocation(this)
                }
                .setNegativeButton("Continue Without") { _, _ ->
                    startSafetyService()
                }
                .show()
        } else {
            binding.tvLocationStatus.text = "✅ ${LocationEnableHelper.getLocationStatusDescription(this)}"
            binding.tvLocationStatus.setTextColor(ContextCompat.getColor(this, R.color.status_active))
            startSafetyService()
        }
    }

    private fun startSafetyService() {
        ShakeDetectionService.start(this)
        updateServiceStatus()
    }

    private fun updateServiceStatus() {
        val isRunning = OEMHelper.isServiceRunning(this, ShakeDetectionService::class.java)
        binding.tvServiceStatus.text = if (isRunning) "✅ Safety monitoring active" else "⚠️ Safety monitoring inactive"
        binding.tvServiceStatus.setTextColor(
            ContextCompat.getColor(this, if (isRunning) R.color.status_active else R.color.status_inactive)
        )

        // Battery optimization status
        if (!OEMHelper.isBatteryOptimizationDisabled(this)) {
            binding.tvBatteryStatus.text = "⚠️ Battery optimization may interfere with alerts"
            binding.tvBatteryStatus.visibility = android.view.View.VISIBLE
        } else {
            binding.tvBatteryStatus.text = "✅ Battery optimization disabled"
            binding.tvBatteryStatus.visibility = android.view.View.VISIBLE
        }
    }

    private fun updateAccessibilityStatus() {
        val isEnabled = VolumeKeyAccessibilityService.isEnabled(this)
        binding.tvAccessibilityStatus.text = if (isEnabled) {
            "✅ Volume button trigger enabled"
        } else {
            "⚠️ Volume button trigger disabled"
        }
        binding.tvAccessibilityStatus.setTextColor(
            ContextCompat.getColor(this, if (isEnabled) R.color.status_active else R.color.status_inactive)
        )

        binding.btnEnableAccessibility.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Enable Volume Button Trigger")
                .setMessage(
                    "To use volume buttons as an emergency trigger from the lock screen, " +
                    "RakshaSetu needs Accessibility Service access.\n\n" +
                    "This is used ONLY to detect volume button presses and nothing else."
                )
                .setPositiveButton("Open Settings") { _, _ ->
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
                .setNegativeButton("Later", null)
                .show()
        }
        binding.btnEnableAccessibility.visibility = if (isEnabled) {
            android.view.View.GONE
        } else {
            android.view.View.VISIBLE
        }
    }

    private fun updateLocationStatus() {
        if (LocationEnableHelper.isLocationEnabled(this)) {
            binding.tvLocationStatus.text = "✅ ${LocationEnableHelper.getLocationStatusDescription(this)}"
            binding.tvLocationStatus.setTextColor(ContextCompat.getColor(this, R.color.status_active))
        } else {
            binding.tvLocationStatus.text = "⚠️ Location services OFF — tap to enable"
            binding.tvLocationStatus.setTextColor(ContextCompat.getColor(this, R.color.warning))
        }
    }
}
