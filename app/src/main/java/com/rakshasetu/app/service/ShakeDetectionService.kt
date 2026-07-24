package com.rakshasetu.app.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.rakshasetu.app.R
import com.rakshasetu.app.RakshaSetuApp
import com.rakshasetu.app.data.entity.AppPreferences
import com.rakshasetu.app.data.repository.PreferencesRepository
import com.rakshasetu.app.domain.trigger.ShakeDetector
import com.rakshasetu.app.domain.trigger.VolumeButtonDetector
import com.rakshasetu.app.ui.countdown.CountdownActivity
import com.rakshasetu.app.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Foreground service that continuously monitors for shake gestures and volume button combos.
 * Uses batched/low-power sensor mode to minimize battery impact.
 * Survives OEM battery killers via foreground service + persistent notification.
 */
@AndroidEntryPoint
class ShakeDetectionService : Service(), ShakeDetector.ShakeListener {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakeDetector
    private var wakeLock: PowerManager.WakeLock? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Cooldown tracking
    private var lastTriggerTime = 0L
    private var isCooldownActive = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val prefs = preferencesRepository.currentPrefs
        shakeDetector = ShakeDetector(
            this,
            ShakeDetector.ShakeConfig(
                threshold = prefs.shakeThreshold,
                eventCount = prefs.shakeEventCount,
                windowMs = prefs.shakeWindowMs,
                sensitivityMultiplier = prefs.triggerSensitivity.shakeThresholdMultiplier
            )
        )

        acquireWakeLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startForeground(NOTIFICATION_ID, createNotification())
                startDetection()
            }
            ACTION_STOP -> {
                stopDetection()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            ACTION_COOLDOWN_START -> {
                isCooldownActive = true
                lastTriggerTime = System.currentTimeMillis()
                serviceScope.launch {
                    delay(preferencesRepository.currentPrefs.cooldownDurationMs)
                    isCooldownActive = false
                }
            }
        }
        return START_STICKY
    }

    private fun startDetection() {
        shakeDetector.reset()
        shakeDetector.start(sensorManager)
    }

    private fun stopDetection() {
        shakeDetector.stop(sensorManager)
    }

    override fun onShakeDetected(eventCount: Int) {
        if (isCooldownActive) return

        // Launch countdown activity
        val intent = Intent(this, CountdownActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            putExtra(CountdownActivity.EXTRA_TRIGGER_TYPE, "shake")
        }
        startActivity(intent)
    }

    override fun onShakePatternInvalid() {
        // Rhythmic pattern detected — false positive, ignore
    }

    private fun createNotification(): Notification {
        val openIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // SOS quick-action button in notification
        val sosIntent = Intent(this, CountdownActivity::class.java).apply {
            putExtra(CountdownActivity.EXTRA_TRIGGER_TYPE, "notification")
        }
        val sosPendingIntent = PendingIntent.getActivity(
            this, 1, sosIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, RakshaSetuApp.CHANNEL_SAFETY)
            .setContentTitle("RakshaSetu Active")
            .setContentText("Safety monitoring is active")
            .setSmallIcon(R.drawable.ic_shield)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_sos, "SOS", sosPendingIntent)
            .build()
    }

    private fun acquireWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "rakshasetu::shake_detection"
        ).apply {
            acquire(60 * 60 * 1000L) // 1 hour max, renewed periodically
        }
    }

    override fun onDestroy() {
        shakeDetector.stop(sensorManager)
        serviceScope.cancel()
        wakeLock?.let { if (it.isHeld) it.release() }
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.rakshasetu.action.START_DETECTION"
        const val ACTION_STOP = "com.rakshasetu.action.STOP_DETECTION"
        const val ACTION_COOLDOWN_START = "com.rakshasetu.action.COOLDOWN_START"
        const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, ShakeDetectionService::class.java).apply {
                action = ACTION_START
            }
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, ShakeDetectionService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        fun startCooldown(context: Context) {
            val intent = Intent(context, ShakeDetectionService::class.java).apply {
                action = ACTION_COOLDOWN_START
            }
            context.startService(intent)
        }
    }
}
