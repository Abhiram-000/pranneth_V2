package com.rakshasetu.app.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils

object OEMHelper {
    /**
     * Detects the device manufacturer/OEM.
     */
    fun getManufacturer(): String = Build.MANUFACTURER.lowercase()

    /**
     * Returns OEM-specific instructions for battery optimization exemption.
     */
    fun getBatteryOptimizationInstructions(context: Context): BatteryInstructions {
        val manufacturer = getManufacturer()
        return when {
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") -> BatteryInstructions(
                manufacturer = "Xiaomi/Redmi",
                steps = listOf(
                    "Go to Settings > Apps > Manage Apps > RakshaSetu",
                    "Tap 'Battery saver' > Select 'No restrictions'",
                    "Go to Settings > Battery & performance > App battery saver",
                    "Find RakshaSetu and disable battery optimization",
                    "Go to Settings > Apps > Manage Apps > RakshaSetu > Auto-start > Enable"
                ),
                hasAutoStartSetting = true
            )
            manufacturer.contains("vivo") -> BatteryInstructions(
                manufacturer = "Vivo",
                steps = listOf(
                    "Go to Settings > Battery > Background power management",
                    "Find RakshaSetu and disable optimization",
                    "Go to Settings > Apps & notifications > See all apps > RakshaSetu",
                    "Tap 'Battery' > Select 'Unrestricted'"
                ),
                hasAutoStartSetting = false
            )
            manufacturer.contains("oppo") || manufacturer.contains("realme") -> BatteryInstructions(
                manufacturer = "OPPO/Realme",
                steps = listOf(
                    "Go to Settings > Battery > More settings",
                    "Tap 'Optimize battery use' > Find RakshaSetu > Turn OFF",
                    "Go to Settings > App management > RakshaSetu > Battery usage",
                    "Allow background activity and auto-launch"
                ),
                hasAutoStartSetting = true
            )
            manufacturer.contains("samsung") -> BatteryInstructions(
                manufacturer = "Samsung",
                steps = listOf(
                    "Go to Settings > Device care > Battery > App power management",
                    "Find RakshaSetu and turn off 'Put apps to sleep'",
                    "Go to Settings > Apps > RakshaSetu > Battery > Select 'Unrestricted'"
                ),
                hasAutoStartSetting = false
            )
            manufacturer.contains("oneplus") -> BatteryInstructions(
                manufacturer = "OnePlus",
                steps = listOf(
                    "Go to Settings > Battery > Battery optimization",
                    "Find RakshaSetu > Select 'Don't optimize'",
                    "Go to Settings > Apps > RakshaSetu > Battery > Background restriction > Remove"
                ),
                hasAutoStartSetting = false
            )
            else -> BatteryInstructions(
                manufacturer = manufacturer.replaceFirstChar { it.uppercase() },
                steps = listOf(
                    "Go to Settings > Battery > Battery optimization",
                    "Find RakshaSetu > Select 'Don't optimize' or 'Unrestricted'",
                    "Also check for any 'Auto-start' or 'Background restriction' settings"
                ),
                hasAutoStartSetting = false
            )
        }
    }

    data class BatteryInstructions(
        val manufacturer: String,
        val steps: List<String>,
        val hasAutoStartSetting: Boolean
    )

    /**
     * Checks if battery optimization is disabled for this app.
     */
    fun isBatteryOptimizationDisabled(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }

    /**
     * Opens battery optimization settings for this app.
     */
    fun openBatteryOptimizationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    /**
     * Opens the app's specific battery optimization page if possible.
     */
    fun openAppBatterySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = android.net.Uri.fromParts("package", context.packageName, null)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            openBatteryOptimizationSettings(context)
        }
    }

    /**
     * Checks if the app has data connection.
     */
    fun hasDataConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Gets current battery level.
     */
    fun getBatteryLevel(context: Context): Int {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    /**
     * Checks if a service is running.
     */
    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        for (service in am.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
