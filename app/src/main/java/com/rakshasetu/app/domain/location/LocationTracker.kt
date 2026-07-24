package com.rakshasetu.app.domain.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback as GmsLocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.rakshasetu.app.data.entity.LocationUpdate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages location tracking using FusedLocationProviderClient.
 * Provides last-known location fast and continuous updates during active alerts.
 */
@Singleton
class LocationTracker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    interface LocationEventListener {
        fun onLocationUpdated(location: LocationUpdate)
        fun onLocationFailed(error: String)
    }

    private val fusedClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private val _lastKnownLocation = MutableStateFlow<Location?>(null)
    val lastKnownLocation: StateFlow<Location?> = _lastKnownLocation.asStateFlow()

    private val _currentAccuracy = MutableStateFlow<Float?>(null)
    val currentAccuracy: StateFlow<Float?> = _currentAccuracy.asStateFlow()

    private var locationEventListener: LocationEventListener? = null
    private var isTracking = false
    private var trackingRequest: LocationRequest? = null
    private var trackingCallback: GmsLocationCallback? = null

    fun setCallback(callback: LocationEventListener) {
        this.locationEventListener = callback
    }

    /**
     * Gets the last known location quickly (from cache/GPS).
     * Falls back to network location if GPS unavailable.
     */
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(callback: (Location?) -> Unit) {
        fusedClient.lastLocation
            .addOnSuccessListener { location ->
                _lastKnownLocation.value = location
                _currentAccuracy.value = location?.accuracy
                callback(location)
            }
            .addOnFailureListener {
                requestSingleUpdate(callback)
            }
    }

    /**
     * Requests a single location update for fast initial fix.
     */
    @SuppressLint("MissingPermission")
    private fun requestSingleUpdate(callback: (Location?) -> Unit) {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
            .setMaxUpdates(1)
            .setDurationMillis(10_000L)
            .build()

        val singleCallback = object : GmsLocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                _lastKnownLocation.value = location
                _currentAccuracy.value = location?.accuracy
                callback(location)
                fusedClient.removeLocationUpdates(this)
            }
        }

        fusedClient.requestLocationUpdates(request, singleCallback, Looper.getMainLooper())
    }

    /**
     * Starts continuous location tracking for active alert.
     * Updates every [intervalMs] for [durationMs] total.
     */
    @SuppressLint("MissingPermission")
    fun startTracking(intervalMs: Long = 120_000L, durationMs: Long = 1_800_000L) {
        if (isTracking) return

        trackingRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, intervalMs)
            .setMaxUpdateDelayMillis(intervalMs * 2)
            .setMinUpdateIntervalMillis(intervalMs / 2)
            .setDurationMillis(durationMs)
            .build()

        trackingCallback = object : GmsLocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    _lastKnownLocation.value = location
                    _currentAccuracy.value = location.accuracy

                    val locationUpdate = LocationUpdate(
                        alertId = 0,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy,
                        source = when (location.provider) {
                            "fused" -> "gps"
                            "gps" -> "gps"
                            "network" -> "network"
                            "passive" -> "passive"
                            else -> location.provider ?: "unknown"
                        }
                    )
                    locationEventListener?.onLocationUpdated(locationUpdate)
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    locationEventListener?.onLocationFailed("Location unavailable")
                }
            }
        }

        fusedClient.requestLocationUpdates(
            trackingRequest!!,
            trackingCallback!!,
            Looper.getMainLooper()
        )
        isTracking = true
    }

    fun stopTracking() {
        trackingCallback?.let { fusedClient.removeLocationUpdates(it) }
        trackingCallback = null
        isTracking = false
    }

    /**
     * Formats a location for SMS messages.
     */
    fun formatLocationForSms(location: Location?): String {
        if (location == null) return "Location unavailable"
        val accuracyText = if (location.accuracy > 0) " (~${location.accuracy.toInt()}m)" else ""
        return "${location.latitude},${location.longitude}$accuracyText"
    }
}
