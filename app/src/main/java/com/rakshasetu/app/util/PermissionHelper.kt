package com.rakshasetu.app.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Centralized permission management for all runtime permissions.
 * Handles Android version differences and provides clear status.
 */
object PermissionHelper {

    val LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val BACKGROUND_LOCATION_PERMISSION = arrayOf(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    val SMS_PERMISSIONS = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_SMS
    )

    val PHONE_PERMISSIONS = arrayOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_PHONE_STATE
    )

    data class PermissionStatus(
        val allGranted: Boolean,
        val missing: List<String>,
        val requiresRationale: List<String>
    )

    /**
     * Checks all critical permissions and returns detailed status.
     */
    fun checkAllPermissions(context: Context): PermissionStatus {
        val allRequired = mutableListOf<String>()
        allRequired.addAll(LOCATION_PERMISSIONS)
        allRequired.addAll(SMS_PERMISSIONS)
        allRequired.addAll(PHONE_PERMISSIONS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            allRequired.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val missing = allRequired.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        val requiresRationale = missing.filter { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)
        }

        return PermissionStatus(
            allGranted = missing.isEmpty(),
            missing = missing,
            requiresRationale = requiresRationale
        )
    }

    /**
     * Checks if critical SMS/Phone permissions are granted.
     */
    fun hasSmsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun hasPhonePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun hasBackgroundLocationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true // Not needed below Android 10
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true // Not needed below Android 13
        }
    }

    /**
     * Gets a human-readable name for each permission.
     */
    fun getPermissionDisplayName(permission: String): String {
        return when (permission) {
            Manifest.permission.SEND_SMS -> "Send SMS"
            Manifest.permission.READ_SMS -> "Read SMS"
            Manifest.permission.CALL_PHONE -> "Make Phone Calls"
            Manifest.permission.READ_PHONE_STATE -> "Read Phone State"
            Manifest.permission.ACCESS_FINE_LOCATION -> "Precise Location (GPS)"
            Manifest.permission.ACCESS_COARSE_LOCATION -> "Approximate Location"
            Manifest.permission.ACCESS_BACKGROUND_LOCATION -> "Background Location"
            Manifest.permission.POST_NOTIFICATIONS -> "Notifications"
            else -> permission.substringAfterLast(".")
        }
    }

    /**
     * Gets the "why" explanation for each permission.
     */
    fun getPermissionExplanation(permission: String): String {
        return when (permission) {
            Manifest.permission.SEND_SMS -> "To send your location and emergency message to contacts"
            Manifest.permission.READ_SMS -> "To confirm delivery of emergency messages"
            Manifest.permission.CALL_PHONE -> "To make emergency calls to contacts and 112"
            Manifest.permission.READ_PHONE_STATE -> "To detect dual-SIM for fallback messaging"
            Manifest.permission.ACCESS_FINE_LOCATION -> "To share your exact GPS location during emergencies"
            Manifest.permission.ACCESS_COARSE_LOCATION -> "To share your approximate location when GPS is unavailable"
            Manifest.permission.ACCESS_BACKGROUND_LOCATION -> "To track and share your location continuously during an active alert"
            Manifest.permission.POST_NOTIFICATIONS -> "To show SOS quick-action and alert status on lock screen"
            else -> "Required for emergency features"
        }
    }

    const val REQUEST_PERMISSIONS = 1000
}
