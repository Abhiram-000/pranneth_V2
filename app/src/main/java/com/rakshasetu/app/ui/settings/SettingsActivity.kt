package com.rakshasetu.app.ui.settings

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.rakshasetu.app.R
import com.rakshasetu.app.data.entity.AppPreferences
import com.rakshasetu.app.data.repository.PreferencesRepository
import com.rakshasetu.app.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    @Inject lateinit var preferencesRepository: PreferencesRepository

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadSettings()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun loadSettings() {
        val prefs = preferencesRepository.currentPrefs

        // Sensitivity
        val sensitivityIndex = when (prefs.triggerSensitivity) {
            AppPreferences.Sensitivity.LOW -> 0
            AppPreferences.Sensitivity.MEDIUM -> 1
            AppPreferences.Sensitivity.HIGH -> 2
        }
        binding.seekbarSensitivity.progress = sensitivityIndex
        binding.tvSensitivityLabel.text = when (prefs.triggerSensitivity) {
            AppPreferences.Sensitivity.LOW -> "Low (fewer false positives)"
            AppPreferences.Sensitivity.MEDIUM -> "Medium (balanced)"
            AppPreferences.Sensitivity.HIGH -> "High (more sensitive)"
        }

        // Countdown duration
        binding.seekbarCountdown.progress = prefs.countdownDurationSeconds - 3 // 3-15 range
        binding.tvCountdownLabel.text = "${prefs.countdownDurationSeconds} seconds"

        // Silent countdown
        binding.switchSilentCountdown.isChecked = prefs.isSilentCountdown

        // SMS template
        binding.etSmsTemplate.setText(prefs.smsTemplate)

        // Duress code
        binding.etDuressCode.setText(prefs.duressCode ?: "")

        // Volume trigger
        binding.switchVolumeTrigger.isChecked = prefs.volumeComboEnabled
    }

    private fun setupListeners() {
        binding.seekbarSensitivity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val sensitivity = when (progress) {
                        0 -> AppPreferences.Sensitivity.LOW
                        1 -> AppPreferences.Sensitivity.MEDIUM
                        else -> AppPreferences.Sensitivity.HIGH
                    }
                    binding.tvSensitivityLabel.text = when (sensitivity) {
                        AppPreferences.Sensitivity.LOW -> "Low (fewer false positives)"
                        AppPreferences.Sensitivity.MEDIUM -> "Medium (balanced)"
                        AppPreferences.Sensitivity.HIGH -> "High (more sensitive)"
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val sensitivity = when (seekBar?.progress ?: 1) {
                    0 -> AppPreferences.Sensitivity.LOW
                    1 -> AppPreferences.Sensitivity.MEDIUM
                    else -> AppPreferences.Sensitivity.HIGH
                }
                preferencesRepository.setSensitivity(sensitivity)
            }
        })

        binding.seekbarCountdown.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.tvCountdownLabel.text = "${progress + 3} seconds"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                preferencesRepository.setCountdownDuration((seekBar?.progress ?: 4) + 3)
            }
        })

        binding.switchSilentCountdown.setOnCheckedChangeListener { _, isChecked ->
            preferencesRepository.setSilentCountdown(isChecked)
            if (isChecked) {
                AlertDialog.Builder(this)
                    .setTitle("Silent Countdown Mode")
                    .setMessage(
                        "In silent mode, there will be NO visible countdown screen. " +
                        "Only a subtle vibration pattern will indicate the countdown is active.\n\n" +
                        "Use this when showing a countdown screen would tip off an attacker."
                    )
                    .setPositiveButton("Got it", null)
                    .show()
            }
        }

        binding.btnSaveTemplate.setOnClickListener {
            val template = binding.etSmsTemplate.text.toString().trim()
            if (template.isBlank()) {
                Toast.makeText(this, "Template cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            preferencesRepository.setSmsTemplate(template)
            Toast.makeText(this, "Template saved", Toast.LENGTH_SHORT).show()
        }

        binding.btnSaveDuressCode.setOnClickListener {
            val code = binding.etDuressCode.text.toString().trim()
            if (code.isNotBlank() && code.length < 4) {
                Toast.makeText(this, "Duress code must be at least 4 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            preferencesRepository.setDuressCode(code.ifBlank { null })
            Toast.makeText(this, if (code.isNotBlank()) "Duress code saved" else "Duress code removed", Toast.LENGTH_SHORT).show()
        }

        binding.switchVolumeTrigger.setOnCheckedChangeListener { _, isChecked ->
            preferencesRepository.update { it.copy(volumeComboEnabled = isChecked) }
        }
    }
}
