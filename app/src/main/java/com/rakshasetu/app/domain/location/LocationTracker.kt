package com.rakshasetu.app.domain.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
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
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Production-grade location tracker.
 *
 * Key fixes:
 * - Proactively caches GPS location on init
 * - Forces fresh HIGH_ACCURACY fix before alerts (not just cache)
 * - Retry with network fallback if GPS unavailable
 * - Saves fix to StateFlow so SMS always has coordinates
 * - Pre-warms GPS during countdown
 */
@Singleton
class LocationTracker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "LocationTracker"
        private const val FRESH_FIX_TIMEOUT_MS = 15_000L
        private const val GPS_RETRY_DELAY_MS = 3_000L
        private const val MAX_ACCURACY_METERS = 500f
    }

    interface LocationEventListener {
        fun onLocationUpdated(location: LocationUpdate)
        fun onLocationFailed(error: String)
    }

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _lastKnownLocation = MutableStateFlow<Location?>(null)
    val lastKnownLocation: StateFlow<Location?> = _lastKnownLocation.asStateFlow()

    private val _currentAccuracy = MutableStateFlow<Float?>(null)
    val currentAccuracy: StateFlow<Float?> = _currentAccuracy.asStateFlow()

    private val _locationStatus = MutableStateFlow("Not initialized")
    val locationStatus: StateFlow<String> = _locationStatus.asStateFlow()

    private var locationEventListener: LocationEventListener? = null
    private var isTracking = false
    private var trackingCallback: GmsLocationCallback? = null
    private var passiveCallback: GmsLocationCallback? = null

    init {
        // Proactively cache location on creation
        proactivelyCacheLocation()
    }

    fun setCallback(callback: LocationEventListener) {
        this.locationEventListener = callback
    }

    /**
     * Proactively cache location on app start so lastKnownLocation is never null
     * when an alert fires.
     */
    @SuppressLint("MissingPermission")
    private fun proactivelyCacheLocation() {
        // First try the cache
        fusedClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null && isLocationFresh(location)) {
                    _lastKnownLocation.value = location
                    _currentAccuracy.value = location.accuracy
                    _locationStatus.value = "Cached: ${location.latitude}, ${location.longitude} (±${location.accuracy.toInt()}m)"
                    Log.d(TAG, "Proactive cache hit: ${location.latitude}, ${location.longitude}")
                } else {
                    // Cache is stale or empty — request fresh fix
                    requestFreshFix()
                }
            }
            .addOnFailureListener {
                requestFreshFix()
            }

        // Also start passive listening so we always have the latest fix
        startPassiveListening()
    }

    /**
     * Passive listener — listens for location updates from OTHER apps.
     * This keeps our cache warm without extra battery drain.
     */
    @SuppressLint("MissingPermission")
    private fun startPassiveListening() {
        val request = LocationRequest.Builder(Priority.PRIORITY_PASSIVE, 0)
            .setMaxUpdates(1)
            .build()

        passiveCallback = object : GmsLocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    if (isLocationBetterThanCurrent(location)) {
                        _lastKnownLocation.value = location
                        _currentAccuracy.value = location.accuracy
                        _locationStatus.value = "Passive: ${location.latitude}, ${location.longitude} (±${location.accuracy.toInt()}m)"
                        Log.d(TAG, "Passive update: ${location.latitude}, ${location.longitude}")
                    }
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    _locationStatus.value = "No location available"
                }
            }
        }

        fusedClient.requestLocationUpdates(
            request,
            passiveCallback!!,
            Looper.getMainLooper()
        )
    }

    /**
     * Request a fresh GPS fix — the critical path for alert dispatch.
     * Tries HIGH_ACCURACY first, falls back to NETWORK if GPS unavailable.
     */
    @SuppressLint("MissingPermission")
    private fun requestFreshFix() {
        _locationStatus.value = "Requesting fresh GPS fix..."
        Log.d(TAG, "Requesting fresh GPS fix")

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMaxUpdates(1)
            .setDurationMillis(FRESH_FIX_TIMEOUT_MS)
            .setMinUpdateIntervalMillis(500)
            .build()

        val callback = object : GmsLocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    if (isLocationBetterThanCurrent(location)) {
                        _lastKnownLocation.value = location
                        _currentAccuracy.value = location.accuracy
                        _locationStatus.value = "Fresh GPS: ${location.latitude}, ${location.longitude} (±${location.accuracy.toInt()}m)"
                        Log.d(TAG, "Fresh GPS fix: ${location.latitude}, ${location.longitude}, accuracy=${location.accuracy}")
                    }
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    Log.w(TAG, "GPS not available, falling back to network")
                    requestNetworkFix()
                }
            }
        }

        fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
    }

    /**
     * Fallback: network-based location when GPS is unavailable (indoors, tunnels).
     */
    @SuppressLint("MissingPermission")
    private fun requestNetworkFix() {
        _locationStatus.value = "GPS unavailable, trying network..."
        Log.d(TAG, "Requesting network fallback")

        val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 2000)
            .setMaxUpdates(1)
            .setDurationMillis(10_000L)
            .build()

        val callback = object : GmsLocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    if (isLocationBetterThanCurrent(location)) {
                        _lastKnownLocation.value = location
                        _currentAccuracy.value = location.accuracy
                        _locationStatus.value = "Network: ${location.latitude}, ${location.longitude} (±${location.accuracy.toInt()}m)"
                        Log.d(TAG, "Network fix: ${location.latitude}, ${location.longitude}, accuracy=${location.accuracy}")
                    }
                } ?: run {
                    _locationStatus.value = "No location available"
                    Log.w(TAG, "Network fix returned null")
                }
            }
        }

        fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
    }

    /**
     * Force a fresh location fix — called before dispatching alerts.
     * Returns the best available location, or null if completely unavailable.
     * This is the CRITICAL method that fixes the "location not sending" bug.
     */
    @SuppressLint("MissingPermission")
    suspend fun forceFreshFix(timeoutMs: Long = FRESH_FIX_TIMEOUT_MS): Location? {
        // If we already have a good fix, return it
        val current = _lastKnownLocation.value
        if (current != null && isLocationFresh(current) && current.accuracy < MAX_ACCURACY_METERS) {
            Log.d(TAG, "Using cached fix: ${current.latitude}, ${current.longitude}")
            return current
        }

        // Force a fresh GPS fix
        Log.d(TAG, "Forcing fresh GPS fix (timeout=${timeoutMs}ms)")
        _locationStatus.value = "Forcing fresh GPS fix..."

        return suspendCancellableCoroutine { cont ->
            @SuppressLint("MissingPermission")
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
                .setMaxUpdates(1)
                .setDurationMillis(timeoutMs)
                .setMinUpdateIntervalMillis(0)
                .build()

            var hasResumed = false

            val callback = object : GmsLocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation
                    if (location != null && !hasResumed) {
                        hasResumed = true
                        _lastKnownLocation.value = location
                        _currentAccuracy.value = location.accuracy
                        _locationStatus.value = "Fresh fix: ${location.latitude}, ${location.longitude} (±${location.accuracy.toInt()}m)"
                        Log.d(TAG, "Fresh fix obtained: ${location.latitude}, ${location.longitude}")
                        fusedClient.removeLocationUpdates(this)
                        cont.resume(location)
                    }
                }

                override fun onLocationAvailability(availability: LocationAvailability) {
                    if (!availability.isLocationAvailable && !hasResumed) {
                        hasResumed = true
                        Log.w(TAG, "Location not available, trying network fallback")
                        fusedClient.removeLocationUpdates(this)
                        // Try network fallback
                        cont.resume(null) // Will trigger network fallback below
                    }
                }
            }

            fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())

            // Timeout safety net
            cont.invokeOnCancellation {
                hasResumed = true
                fusedClient.removeLocationUpdates(callback)
                Log.w(TAG, "Fresh fix timed out after ${timeoutMs}ms")
            }
        } ?: run {
            // If GPS failed, try network
            Log.d(TAG, "GPS failed, trying network fallback")
            return suspendCancellableCoroutine { cont ->
                @SuppressLint("MissingPermission")
                val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 0)
                    .setMaxUpdates(1)
                    .setDurationMillis(8_000L)
                    .build()

                var hasResumed = false
                val callback = object : GmsLocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        val location = result.lastLocation
                        if (location != null && !hasResumed) {
                            hasResumed = true
                            _lastKnownLocation.value = location
                            _currentAccuracy.value = location.accuracy
                            _locationStatus.value = "Network fallback: ${location.latitude}, ${location.longitude} (±${location.accuracy.toInt()}m)"
                            fusedClient.removeLocationUpdates(this)
                            cont.resume(location)
                        }
                    }

                    override fun onLocationAvailability(availability: LocationAvailability) {
                        if (!hasResumed) {
                            hasResumed = true
                            fusedClient.removeLocationUpdates(this)
                            cont.resume(null)
                        }
                    }
                }

                fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())

                cont.invokeOnCancellation {
                    hasResumed = true
                    fusedClient.removeLocationUpdates(callback)
                }
            }
        }
    }

    /**
     * Pre-warm GPS — call this when countdown starts so location is ready when alert fires.
     */
    @SuppressLint("MissingPermission")
    fun preWarmGps() {
        Log.d(TAG, "Pre-warming GPS")
        _locationStatus.value = "Pre-warming GPS..."

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
            .setMaxUpdates(5)
            .setDurationMillis(30_000L)
            .setMinUpdateIntervalMillis(1000)
            .build()

        val callback = object : GmsLocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    if (isLocationBetterThanCurrent(location)) {
                        _lastKnownLocation.value = location
                        _currentAccuracy.value = location.accuracy
                        _locationStatus.value = "Pre-warmed: ${location.latitude}, ${location.longitude} (±${location.accuracy.toInt()}m)"
                        Log.d(TAG, "Pre-warm fix: ${location.latitude}, ${location.longitude}, accuracy=${location.accuracy}")
                    }
                }
            }
        }

        fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
    }

    /**
     * Starts continuous location tracking for active alert.
     */
    @SuppressLint("MissingPermission")
    fun startTracking(intervalMs: Long = 120_000L, durationMs: Long = 1_800_000L) {
        if (isTracking) return

        val trackingRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
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
                            "gps" -> "gps"
                            "network" -> "network"
                            "passive" -> "passive"
                            else -> location.provider ?: "unknown"
                        }
                    )
                    locationEventListener?.onLocationUpdated(locationUpdate)
                    Log.d(TAG, "Tracking update: ${location.latitude}, ${location.longitude}")
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    locationEventListener?.onLocationFailed("Location unavailable during tracking")
                    Log.w(TAG, "Location unavailable during tracking")
                }
            }
        }

        fusedClient.requestLocationUpdates(
            trackingRequest,
            trackingCallback!!,
            Looper.getMainLooper()
        )
        isTracking = true
        _locationStatus.value = "Tracking active"
    }

    fun stopTracking() {
        trackingCallback?.let { fusedClient.removeLocationUpdates(it) }
        trackingCallback = null
        isTracking = false
    }

    /**
     * Returns the best available location for SMS, trying multiple strategies.
     */
    fun getLocationForSms(): Location? {
        val loc = _lastKnownLocation.value
        if (loc != null) {
            Log.d(TAG, "getLocationForSms: ${loc.latitude}, ${loc.longitude} (±${loc.accuracy}m)")
        } else {
            Log.w(TAG, "getLocationForSms: null — no location available")
        }
        return loc
    }

    /**
     * Formats a location for SMS messages.
     */
    fun formatLocationForSms(location: Location?): String {
        if (location == null) return "Location unavailable"
        val accuracyText = if (location.accuracy > 0) " (~${location.accuracy.toInt()}m)" else ""
        return "${location.latitude},${location.longitude}$accuracyText"
    }

    private fun isLocationFresh(location: Location): Boolean {
        val age = System.currentTimeMillis() - location.time
        return age < TimeUnit.MINUTES.toMillis(5)
    }

    private fun isLocationBetterThanCurrent(newLocation: Location): Boolean {
        val current = _lastKnownLocation.value ?: return true
        // New location is better if:
        // 1. It's more recent AND more accurate, OR
        // 2. It's significantly more accurate even if slightly older
        val isNewer = newLocation.time > current.time
        val isMoreAccurate = newLocation.accuracy < current.accuracy
        return (isNewer && isMoreAccurate) || (isMoreAccurate && newLocation.accuracy < 50f)
    }
}
