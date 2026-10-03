package com.rakshasetu.app.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.rakshasetu.app.R
import com.rakshasetu.app.RakshaSetuApp
import com.rakshasetu.app.data.dao.LocationUpdateDao
import com.rakshasetu.app.data.entity.LocationUpdate
import com.rakshasetu.app.data.repository.AlertRepository
import com.rakshasetu.app.data.repository.ContactRepository
import com.rakshasetu.app.data.repository.PreferencesRepository
import com.rakshasetu.app.domain.location.LocationSmsFormatter
import com.rakshasetu.app.domain.location.LocationTracker
import com.rakshasetu.app.domain.sms.SMSDispatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Foreground service that provides continuing location updates during an active alert.
 * Sends location pings every 2-3 minutes for up to 30 minutes.
 * This allows contacts to track movement, not just the starting point.
 */
@AndroidEntryPoint
class LocationTrackingService : Service(), LocationTracker.LocationEventListener {

    @Inject lateinit var alertRepository: AlertRepository
    @Inject lateinit var preferencesRepository: PreferencesRepository
    @Inject lateinit var locationTracker: LocationTracker
    @Inject lateinit var locationUpdateDao: LocationUpdateDao
    @Inject lateinit var contactRepository: ContactRepository
    @Inject lateinit var smsDispatcher: SMSDispatcher

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var wakeLock: PowerManager.WakeLock? = null
    private var alertId: Long = -1

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        locationTracker.setCallback(this)
        acquireWakeLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                alertId = intent.getLongExtra(EXTRA_ALERT_ID, -1)
                val prefs = preferencesRepository.currentPrefs

                startForeground(NOTIFICATION_ID, createNotification(),
                    android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
                locationTracker.startTracking(
                    intervalMs = prefs.locationUpdateIntervalMs,
                    durationMs = prefs.locationUpdateDurationMs
                )

                // Auto-stop after maximum duration
                serviceScope.launch {
                    delay(prefs.locationUpdateDurationMs + 60_000L) // 1 min buffer
                    stopSelf()
                }
            }
            ACTION_STOP -> {
                locationTracker.stopTracking()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onLocationUpdated(update: LocationUpdate) {
        serviceScope.launch {
            val row = update.copy(alertId = alertId)
            // Persist the trail for post-incident review.
            try { locationUpdateDao.insertLocation(row) } catch (_: Exception) {}

            // Push the position to every emergency contact over SMS.
            val body = buildString {
                append("RakshaSetu update — location: ")
                append(LocationSmsFormatter.format(row.latitude, row.longitude, row.accuracy, row.source))
                append(". Stay safe. — RakshaSetu")
            }
            val contacts = contactRepository.getAllContactsList()
            var code = alertId.toInt().coerceAtLeast(0) * 10 + 3
            for (c in contacts) {
                code++
                try {
                    smsDispatcher.sendSms(
                        contact = c,
                        message = body,
                        sentRequestCode = code,
                        deliveredRequestCode = code + 50
                    )
                } catch (_: Exception) {}
            }
        }
    }

    override fun onLocationFailed(error: String) {
        // Best effort — continue trying
    }

    private fun acquireWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "rakshasetu::location_tracking"
        ).apply {
            acquire(60 * 60 * 1000L) // 1 hour max
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, RakshaSetuApp.CHANNEL_SAFETY)
            .setContentTitle("Location tracking active")
            .setContentText("Sharing location with emergency contacts")
            .setSmallIcon(R.drawable.ic_location)
            .setOngoing(true)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    override fun onDestroy() {
        locationTracker.stopTracking()
        serviceScope.cancel()
        wakeLock?.let { if (it.isHeld) it.release() }
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.rakshasetu.action.START_LOCATION"
        const val ACTION_STOP = "com.rakshasetu.action.STOP_LOCATION"
        const val EXTRA_ALERT_ID = "alert_id"
        const val NOTIFICATION_ID = 1003
    }
}
