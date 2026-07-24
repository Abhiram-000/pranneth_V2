package com.rakshasetu.app.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.rakshasetu.app.R
import com.rakshasetu.app.RakshaSetuApp
import com.rakshasetu.app.data.entity.AlertLog
import com.rakshasetu.app.data.entity.AppPreferences
import com.rakshasetu.app.data.entity.EmergencyContact
import com.rakshasetu.app.data.repository.AlertRepository
import com.rakshasetu.app.data.repository.ContactRepository
import com.rakshasetu.app.data.repository.PreferencesRepository
import com.rakshasetu.app.domain.call.CallManager
import com.rakshasetu.app.domain.escalation.EscalationManager
import com.rakshasetu.app.domain.location.LocationTracker
import com.rakshasetu.app.domain.sms.SMSDispatcher
import com.rakshasetu.app.ui.countdown.CountdownActivity
import com.rakshasetu.app.util.AlertUtils
import com.rakshasetu.app.util.OEMHelper
import com.rakshasetu.app.util.SmsVerificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Core service that dispatches emergency alerts via SMS and missed calls.
 * Handles:
 * - SMS to all emergency contacts with retry logic
 * - Missed calls to contacts sequentially
 * - SMS/call to 112 (ERSS)
 * - Exponential backoff retry on failure
 * - Dual-SIM fallback (if available)
 * - Escalation to next contact if primary unreachable
 * - Continuing location updates during active alert
 */
@AndroidEntryPoint
class AlertDispatchService : Service(), CallManager.CallCallback {

    @Inject lateinit var alertRepository: AlertRepository
    @Inject lateinit var contactRepository: ContactRepository
    @Inject lateinit var preferencesRepository: PreferencesRepository
    @Inject lateinit var smsDispatcher: SMSDispatcher
    @Inject lateinit var callManager: CallManager
    @Inject lateinit var locationTracker: LocationTracker
    @Inject lateinit var escalationManager: EscalationManager

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var wakeLock: PowerManager.WakeLock? = null
    private var activeAlertId: Long = -1

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        callManager.setCallback(this)
        acquireWakeLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_DISPATCH_ALERT -> {
                val alertId = intent.getLongExtra(EXTRA_ALERT_ID, -1)
                val isAirplaneMode = intent.getBooleanExtra(EXTRA_IS_AIRPLANE_MODE, false)
                if (alertId != -1L) {
                    startForeground(NOTIFICATION_ID, createNotification("Dispatching emergency alert..."))
                    serviceScope.launch {
                        dispatchAlert(alertId, isAirplaneMode)
                    }
                }
            }
            ACTION_CANCEL_ALERT -> {
                val alertId = intent.getLongExtra(EXTRA_ALERT_ID, -1)
                val isDuress = intent.getBooleanExtra(EXTRA_IS_DURESS, false)
                serviceScope.launch {
                    cancelAlert(alertId, isDuress)
                }
            }
            ACTION_STOP -> {
                cleanupAndStop()
            }
        }
        return START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    private suspend fun dispatchAlert(alertId: Long, isAirplaneMode: Boolean) {
        activeAlertId = alertId
        val prefs = preferencesRepository.currentPrefs
        val contacts = contactRepository.getAllContactsList()

        if (contacts.isEmpty() && !isAirplaneMode) {
            // No contacts to notify — just alert 112
            dispatchToEmergencyNumber(alertId, prefs)
            cleanupAndStop()
            return
        }

        // Get current location for the alert
        var location = locationTracker.lastKnownLocation.value
        if (location == null) {
            location = withContext(Dispatchers.Main) {
                kotlin.coroutines.suspendCoroutine { cont ->
                    locationTracker.getLastKnownLocation { loc ->
                        cont.resumeWith(Result.success(loc))
                    }
                }
            }
        }

        // Format SMS message
        val batteryLevel = OEMHelper.getBatteryLevel(this)
        val hasData = OEMHelper.hasDataConnection(this)
        val message = SmsVerificationHelper.formatEmergencyMessage(
            template = prefs.smsTemplate,
            latitude = location?.latitude,
            longitude = location?.longitude,
            accuracy = location?.accuracy,
            batteryLevel = batteryLevel,
            hasData = hasData
        )

        // Start location tracking for continuing updates
        locationTracker.setCallback(object : LocationTracker.LocationEventListener {
            override fun onLocationUpdated(update: com.rakshasetu.app.data.entity.LocationUpdate) {
                // Location updates continue in background — contacts can track movement
            }
            override fun onLocationFailed(error: String) {
                // Best effort — continue without location updates
            }
        })
        locationTracker.startTracking(
            intervalMs = prefs.locationUpdateIntervalMs,
            durationMs = prefs.locationUpdateDurationMs
        )

        // Start escalation manager
        escalationManager.startEscalation(contacts)

        // Dispatch to all contacts with retry
        var sentCount = 0
        for (contact in contacts) {
            val success = dispatchToContact(contact, message, alertId, sentCount)
            if (success) {
                sentCount++
                alertRepository.incrementSmsCount(alertId)
            }
        }

        // Dispatch to emergency number (112)
        dispatchToEmergencyNumber(alertId, prefs)

        // Start missed calls to contacts (after SMS)
        for (contact in contacts) {
            try {
                callManager.placeMissedCall(contact, ringDurationMs = 5000L)
                alertRepository.incrementCallCount(alertId)
                delay(6000L) // Wait between calls
            } catch (e: Exception) {
                // Best effort — continue with next contact
            }
        }

        // Start background location update service
        val locationIntent = Intent(this, LocationTrackingService::class.java).apply {
            action = LocationTrackingService.ACTION_START
            putExtra(LocationTrackingService.EXTRA_ALERT_ID, alertId)
        }
        startForegroundService(locationIntent)

        // Update notification
        updateNotification("Alert active — ${sentCount} contacts notified")
    }

    /**
     * Dispatches SMS to a single contact with exponential backoff retry.
     */
    private suspend fun dispatchToContact(
        contact: EmergencyContact,
        message: String,
        alertId: Long,
        sentCount: Int
    ): Boolean {
        val maxRetries = 3
        val retryDelays = longArrayOf(10_000L, 30_000L, 90_000L)

        for (attempt in 0 until maxRetries) {
            val success = smsDispatcher.sendSms(
                contact = contact,
                message = message,
                sentRequestCode = sentCount * 100 + attempt,
                deliveredRequestCode = sentCount * 100 + attempt + 50
            )

            if (success) return true

            // Wait before retry with exponential backoff
            if (attempt < maxRetries - 1) {
                delay(retryDelays[attempt])
            }
        }
        return false
    }

    /**
     * Dispatches SMS and call to the emergency number (112/ERSS).
     */
    @SuppressLint("MissingPermission")
    private suspend fun dispatchToEmergencyNumber(alertId: Long, prefs: AppPreferences) {
        try {
            val location = locationTracker.lastKnownLocation.value
            val batteryLevel = OEMHelper.getBatteryLevel(this)
            val hasData = OEMHelper.hasDataConnection(this)

            val emergencyMessage = buildString {
                append("EMERGENCY SOS from RakshaSetu app. ")
                append("I need immediate help. ")
                location?.let {
                    append("Location: https://maps.google.com/?q=${it.latitude},${it.longitude}")
                    append(" (accuracy: ~${it.accuracy.toInt()}m). ")
                }
                append("Battery: $batteryLevel%. ")
                if (!hasData) append("No data connection. ")
                append("This is an automated emergency alert.")
            }

            smsDispatcher.sendEmergencySms(
                emergencyNumber = prefs.emergencyNumber,
                message = emergencyMessage,
                sentRequestCode = 9999
            )

            // Also place a call to 112
            callManager.placeEmergencyCall(
                emergencyNumber = prefs.emergencyNumber,
                ringDurationMs = 8000L
            )
        } catch (e: Exception) {
            // Best effort — continue with contact notifications
        }
    }

    /**
     * Cancels an active alert.
     * If duress mode, the cancel appears to work but alert continues silently.
     */
    private suspend fun cancelAlert(alertId: Long, isDuress: Boolean) {
        val prefs = preferencesRepository.currentPrefs

        if (isDuress && prefs.duressCode != null) {
            // Duress cancel — don't actually stop the alert
            // The SMS/call pipeline continues silently
            alertRepository.updateAlert(
                AlertLog(
                    id = alertId,
                    triggerType = AlertLog.TRIGGER_MANUAL,
                    isDuress = true
                )
            )
            return
        }

        // Normal cancel
        alertRepository.endAlert(alertId, isCancelled = true, cancelMethod = "user_cancel")
        locationTracker.stopTracking()
        callManager.cancelPendingHangup()
        escalationManager.stopEscalation()
        ShakeDetectionService.startCooldown(this)

        cleanupAndStop()
    }

    private fun cleanupAndStop() {
        locationTracker.stopTracking()
        callManager.cancelPendingHangup()
        serviceScope.cancel()
        wakeLock?.let { if (it.isHeld) it.release() }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun acquireWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "rakshasetu::alert_dispatch"
        ).apply {
            acquire(30 * 60 * 1000L) // 30 minutes max
        }
    }

    private fun createNotification(text: String): Notification {
        val cancelIntent = Intent(this, AlertDispatchService::class.java).apply {
            action = ACTION_STOP
        }
        val cancelPending = PendingIntent.getService(
            this, 0, cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, RakshaSetuApp.CHANNEL_ALERT)
            .setContentTitle("🚨 EMERGENCY ALERT ACTIVE")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_sos)
            .setOngoing(true)
            .setSilent(false)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_cancel, "Cancel Alert", cancelPending)
            .build()
    }

    private fun updateNotification(text: String) {
        val notification = createNotification(text)
        val nm = getSystemService(android.app.NotificationManager::class.java)
        nm.notify(NOTIFICATION_ID, notification)
    }

    // CallManager callbacks
    override fun onCallStarted(contactId: Long) {}
    override fun onCallEnded(contactId: Long) {}
    override fun onCallFailed(contactId: Long, error: String) {}

    companion object {
        const val ACTION_DISPATCH_ALERT = "com.rakshasetu.action.DISPATCH_ALERT"
        const val ACTION_CANCEL_ALERT = "com.rakshasetu.action.CANCEL_ALERT"
        const val ACTION_STOP = "com.rakshasetu.action.STOP_DISPATCH"
        const val EXTRA_ALERT_ID = "alert_id"
        const val EXTRA_IS_AIRPLANE_MODE = "is_airplane_mode"
        const val EXTRA_IS_DURESS = "is_duress"
        const val NOTIFICATION_ID = 1002

        fun dispatchAlert(context: Context, alertId: Long, isAirplaneMode: Boolean = false) {
            val intent = Intent(context, AlertDispatchService::class.java).apply {
                action = ACTION_DISPATCH_ALERT
                putExtra(EXTRA_ALERT_ID, alertId)
                putExtra(EXTRA_IS_AIRPLANE_MODE, isAirplaneMode)
            }
            context.startForegroundService(intent)
        }

        fun cancelAlert(context: Context, alertId: Long, isDuress: Boolean = false) {
            val intent = Intent(context, AlertDispatchService::class.java).apply {
                action = ACTION_CANCEL_ALERT
                putExtra(EXTRA_ALERT_ID, alertId)
                putExtra(EXTRA_IS_DURESS, isDuress)
            }
            context.startService(intent)
        }
    }
}
