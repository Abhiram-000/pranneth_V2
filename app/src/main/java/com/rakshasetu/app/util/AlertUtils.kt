package com.rakshasetu.app.util

import android.content.Context
import android.location.Location
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object AlertUtils {

    /**
     * Generates a distinct vibration pattern for the countdown.
     * Pattern: short-long-short-long to be recognizable through fabric.
     */
    fun vibrateCountdownPattern(context: Context) {
        val pattern = longArrayOf(0, 200, 100, 400, 100, 200, 100, 400)
        vibrate(context, pattern, 0)
    }

    /**
     * Generates a confirmation vibration (single long buzz).
     */
    fun vibrateConfirmation(context: Context) {
        val pattern = longArrayOf(0, 500)
        vibrate(context, pattern, 0)
    }

    /**
     * Generates a cancel vibration (short double tap).
     */
    fun vibrateCancel(context: Context) {
        val pattern = longArrayOf(0, 100, 50, 100)
        vibrate(context, pattern, 0)
    }

    /**
     * Stops any ongoing vibration.
     */
    fun stopVibration(context: Context) {
        val vibrator = getVibrator(context)
        vibrator?.cancel()
    }

    private fun vibrate(context: Context, pattern: LongArray, repeat: Int) {
        val vibrator = getVibrator(context)
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createWaveform(pattern, repeat))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(pattern, repeat)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    /**
     * Formats battery and connectivity status for SMS.
     */
    fun formatStatusForSms(batteryLevel: Int?, hasData: Boolean): String {
        val parts = mutableListOf<String>()
        batteryLevel?.let { parts.add("Battery: $it%") }
        if (!hasData) parts.add("No data connection")
        return if (parts.isNotEmpty()) "Status: ${parts.joinToString(", ")}" else ""
    }
}
