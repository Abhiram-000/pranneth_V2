package com.rakshasetu.app.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.rakshasetu.app.R
import com.rakshasetu.app.data.entity.AlertLog
import com.rakshasetu.app.domain.call.CallManager
import com.rakshasetu.app.domain.escalation.EscalationManager
import com.rakshasetu.app.domain.location.LocationTracker
import com.rakshasetu.app.domain.sms.SMSDispatcher
import com.rakshasetu.app.data.repository.AlertRepository
import com.rakshasetu.app.data.repository.ContactRepository
import com.rakshasetu.app.data.repository.PreferencesRepository
import com.rakshasetu.app.ui.main.MainActivity
import com.rakshasetu.app.util.OEMHelper
import com.rakshasetu.app.util.SmsVerificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Service that dispatches emergency SMS and calls to all registered contacts.
 *
 * Key fixes for location:
 * - Force a fresh GPS fix BEFORE formatting SMS
 * - Wait for location with timeout (up to 15s)
 * - Save location coordinates to AlertLog so it's persisted
 * - Network fallback if GPS unavailable
 */
@AndroidEntryPoint
class AlertDispatchService : LifecycleService() {

    companion object {
        private const val TAG = "AlertDispatch"
        private const val NOTIFICATION_CHANNEL = "alert_dispatch"
        private const val NOTIFICATION_ID = 7001
        const val ACTION_DISPATCH_ALERT = "com.rakshasetu.DISPATCH_ALERT"
        const val EXTRA_TRIGGER_TYPE = "trigger_type"
        const val EXTRA_IS_SILENT = "is_silent"
        const val EXTRA_IS_DURESS = "is_duress"
        const val EXTRA_ALERT_ID = "alert_id"
        const val EXTRA_IS_AIRPLANE_MODE = "is_airplane_mode"

        fun dispatchAlert(context: Context, triggerType: String, isSilent: Boolean = false, isDuress: Boolean = false) {
            val intent = Intent(context, AlertDispatchService::class.java).apply {
                action = ACTION_DISPATCH_ALERT
                putExtra(EXTRA_TRIGGER_TYPE, triggerType)
                putExtra(EXTRA_IS_SILENT, isSilent)
                putExtra(EXTRA_IS_DURESS, isDuress)
            }
            context.startForegroundService(intent)
        }
    }

    @Inject lateinit var smsDispatcher: SMSDispatcher
    @Inject lateinit var callManager: CallManager
    @Inject lateinit var contactRepository: ContactRepository
    @Inject lateinit var alertRepository: AlertRepository
    @Inject lateinit var locationTracker: LocationTracker
    @Inject lateinit var preferencesRepository: PreferencesRepository
    @Inject lateinit var escalationManager: EscalationManager

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val notification = buildForegroundNotification("Preparing emergency alert...")
        startForeground(
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION or ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )

        when (intent?.action) {
            ACTION_DISPATCH_ALERT -> {
                val triggerType = intent.getStringExtra(EXTRA_TRIGGER_TYPE) ?: "unknown"
                val isSilent = intent.getBooleanExtra(EXTRA_IS_SILENT, false)
                val isDuress = intent.getBooleanExtra(EXTRA_IS_DURESS, false)
                lifecycleScope.launch {
                    dispatchAlert(triggerType, isSilent, isDuress)
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    @SuppressLint("MissingPermission")
    private suspend fun dispatchAlert(triggerType: String, isSilent: Boolean, isDuress: Boolean = false) {
        val startTime = System.currentTimeMillis()

        try {
            updateNotification("Obtaining GPS location...")

            // CRITICAL FIX: Force a fresh GPS fix BEFORE formatting the SMS
            // This is what was missing — previously we just read the cache which was null
            Log.d(TAG, "Forcing fresh GPS fix before alert dispatch...")
            val freshLocation = locationTracker.forceFreshFix(timeoutMs = 15_000L)

            if (freshLocation != null) {
                Log.d(TAG, "Fresh fix obtained: ${freshLocation.latitude}, ${freshLocation.longitude} (±${freshLocation.accuracy}m)")
                updateNotification("Location obtained: ±${freshLocation.accuracy.toInt()}m accuracy")
            } else {
                Log.w(TAG, "Fresh fix failed — alert will send with last known or no location")
                updateNotification("Location unavailable — using last known position")
            }

            val location = locationTracker.lastKnownLocation.value
            val contacts = contactRepository.getAllContactsList()
            val prefs = preferencesRepository.currentPrefs

            if (contacts.isEmpty()) {
                Log.w(TAG, "No emergency contacts configured")
                updateNotification("No contacts configured — dispatching to 112 only")
            }

            // Build message with location
            val batteryLevel = OEMHelper.getBatteryLevel(this)
            val hasData = OEMHelper.hasDataConnection(this)

            val message = SmsVerificationHelper.formatEmergencyMessage(
                template = prefs.smsTemplate,
                latitude = location?.latitude,
                longitude = location?.longitude,
                accuracy = location?.accuracy,
                batteryLevel = batteryLevel,
                hasData = hasData,
                provider = location?.provider
            )

            Log.d(TAG, "Alert message: $message")

            // Create alert log entry WITH location saved
            val alertLog = AlertLog(
                triggerType = triggerType,
                latitude = location?.latitude,
                longitude = location?.longitude,
                accuracy = location?.accuracy,
                batteryLevel = batteryLevel,
                hasDataConnection = hasData,
                isDuress = isDuress,
                cancelMethod = if (isDuress) "duress" else null
            )
            val alertId = alertRepository.createAlert(alertLog)
            Log.d(TAG, "Alert logged with ID: $alertId")

            updateNotification("Alert active — sending to ${contacts.size} contacts")

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
            dispatchToEmergencyNumber(alertId, prefs, location)

            // Start missed calls to contacts (after SMS)
            for (contact in contacts) {
                try {
                    callManager.placeMissedCall(contact, ringDurationMs = 5000L)
                    alertRepository.incrementCallCount(alertId)
                    delay(6000L)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed missed call to ${contact.name}", e)
                }
            }

            // Start background location update service
            val locationIntent = Intent(this, LocationTrackingService::class.java).apply {
                action = LocationTrackingService.ACTION_START
                putExtra(LocationTrackingService.EXTRA_ALERT_ID, alertId)
            }
            startForegroundService(locationIntent)

            // Update notification
            updateNotification("Alert active — $sentCount contacts notified, location tracking")

            // Log final status
            val elapsed = System.currentTimeMillis() - startTime
            Log.d(TAG, "Alert dispatch complete in ${elapsed}ms: $sentCount SMS sent, location=${location?.latitude ?: "null"}")

        } catch (e: Exception) {
            Log.e(TAG, "Alert dispatch failed", e)
            updateNotification("Alert failed: ${e.message}")
        }
    }

    private suspend fun dispatchToContact(
        contact: com.rakshasetu.app.data.entity.EmergencyContact,
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

            if (attempt < maxRetries - 1) {
                delay(retryDelays[attempt])
            }
        }
        return false
    }

    @SuppressLint("MissingPermission")
    private suspend fun dispatchToEmergencyNumber(
        alertId: Long,
        prefs: com.rakshasetu.app.data.entity.AppPreferences,
        location: android.location.Location?
    ) {
        try {
            val batteryLevel = OEMHelper.getBatteryLevel(this)
            val hasData = OEMHelper.hasDataConnection(this)

            val emergencyMessage = buildString {
                append("EMERGENCY SOS from RakshaSetu app. ")
                append("I need immediate help. ")
                location?.let {
                    append("Location: https://maps.google.com/?q=${it.latitude},${it.longitude}")
                    append(" (accuracy: ~${it.accuracy.toInt()}m). ")
                } ?: append("Location unavailable. ")
                append("Battery: $batteryLevel%. ")
                if (!hasData) append("No data connection. ")
                append("This is an automated emergency alert.")
            }

            smsDispatcher.sendEmergencySms(
                emergencyNumber = prefs.emergencyNumber,
                message = emergencyMessage,
                sentRequestCode = 9999
            )

            callManager.placeEmergencyCall(
                emergencyNumber = prefs.emergencyNumber,
                ringDurationMs = 8000L
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to dispatch to emergency number", e)
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            "Emergency Alert",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Shows when an emergency alert is being dispatched"
            setShowBadge(false)
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildForegroundNotification(text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setContentTitle("RakshaSetu Alert Active")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_sos)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun updateNotification(text: String) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        val notification = buildForegroundNotification(text)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
