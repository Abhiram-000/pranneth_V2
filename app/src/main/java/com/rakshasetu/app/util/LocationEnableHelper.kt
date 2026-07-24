package com.rakshasetu.app.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Ensures GPS/location services are enabled before attempting location fixes.
 * Handles the full flow: check → prompt user → verify → fallback.
 */
object LocationEnableHelper {

    /**
     * Checks if location services (GPS + network) are enabled on the device.
     */
    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return isGpsEnabled || isNetworkEnabled
    }

    /**
     * Checks specifically if GPS provider is enabled (high accuracy).
     */
    fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    /**
     * Attempts to enable high-accuracy location via system settings.
     * Returns true if location is already enabled or was successfully enabled.
     */
    fun promptEnableLocation(activity: Activity): Boolean {
        if (isLocationEnabled(activity)) return true

        try {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
            return true
        } catch (e: Exception) {
            // Fallback: try to open app settings
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", activity.packageName, null)
                activity.startActivity(intent)
            } catch (_: Exception) {}
            return false
        }
    }

    /**
     * Requests location settings check using Google Play Services.
     * This will show a dialog asking the user to enable high-accuracy location.
     * Returns true if settings are already satisfied.
     */
    @SuppressLint("MissingPermission")
    suspend fun requestLocationSettings(activity: Activity): Boolean {
        return suspendCancellableCoroutine { cont ->
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setDurationMillis(10_000)
                .build()

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true)

            val client = LocationServices.getSettingsClient(activity)
            val task = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                cont.resume(true)
            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(activity, REQUEST_LOCATION_SETTINGS)
                        // Resume after settings dialog
                        cont.resume(false)
                    } catch (_: Exception) {
                        cont.resume(false)
                    }
                } else {
                    cont.resume(false)
                }
            }
        }
    }

    /**
     * Gets a user-friendly description of current location status.
     */
    fun getLocationStatusDescription(context: Context): String {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        return when {
            isGpsEnabled && isNetworkEnabled -> "High accuracy (GPS + Network)"
            isGpsEnabled -> "GPS only (may be slower indoors)"
            isNetworkEnabled -> "Network only (~500m accuracy)"
            else -> "Location services OFF"
        }
    }

    const val REQUEST_LOCATION_SETTINGS = 1001
}
