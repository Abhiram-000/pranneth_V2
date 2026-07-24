package com.rakshasetu.app.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import com.rakshasetu.app.ui.contacts.ContactListActivity
import com.rakshasetu.app.ui.countdown.CountdownActivity
import com.rakshasetu.app.ui.settings.SettingsActivity
import com.rakshasetu.app.util.OEMHelper
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
        checkPermissionsAndStartService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeContactCount()
        checkPermissionsAndStartService()
    }

    private fun setupUI() {
        // SOS Button — triggers countdown
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

        // Accessibility service status
        updateAccessibilityStatus()

        // Service status
        updateServiceStatus()
    }

    private fun observeContactCount() {
        lifecycleScope.launch {
            contactRepository.getContactCount().collectLatest { count ->
                binding.tvContactCount.text = "$count emergency contact(s) configured"
                binding.btnSos.isEnabled = count > 0
                if (count == 0) {
                    binding.tvWarning.text = "Add at least one emergency contact to enable SOS"
                    binding.tvWarning.visibility = android.view.View.VISIBLE
                } else {
                    binding.tvWarning.visibility = android.view.View.GONE
                }
            }
        }
    }

    private fun checkPermissionsAndStartService() {
        val requiredPermissions = mutableListOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.VIBRATE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requiredPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            // Show rationale dialog before requesting
            AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage(
                    "RakshaSetu needs these permissions to protect you:\n\n" +
                    "• SMS: To send emergency alerts to your contacts\n" +
                    "• Phone: To make emergency calls\n" +
                    "• Location: To share your location during emergencies\n" +
                    "• Notifications: To show SOS quick action and service status"
                )
                .setPositiveButton("Grant Permissions") { _, _ ->
                    permissionLauncher.launch(missingPermissions.toTypedArray())
                }
                .setNegativeButton("Later") { _, _ ->
                    Toast.makeText(this, "Some features may not work without permissions", Toast.LENGTH_LONG).show()
                }
                .show()
        } else {
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

        if (!isEnabled) {
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
            binding.btnEnableAccessibility.visibility = android.view.View.VISIBLE
        } else {
            binding.btnEnableAccessibility.visibility = android.view.View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        updateServiceStatus()
        updateAccessibilityStatus()
    }
}
