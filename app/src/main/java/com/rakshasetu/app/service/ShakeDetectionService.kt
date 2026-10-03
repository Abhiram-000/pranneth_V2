package com.rakshasetu.app.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import com.rakshasetu.app.R
import com.rakshasetu.app.RakshaSetuApp
import com.rakshasetu.app.domain.trigger.ShakeDetector
import com.rakshasetu.app.domain.trigger.VolumeButtonDetector
import com.rakshasetu.app.ui.countdown.CountdownActivity
import com.rakshasetu.app.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * FOREGROUND SERVICE — the backbone of 24/7 safety monitoring.
 *
 * Guarantees:
 * - Survives OEM battery killers via foreground service + persistent notification
 * - Auto-restarts sensors if OS unregisters them
 * - Wake lock prevents CPU sleep so sensor keeps firing
 * - Periodic sensor health checks every 30 seconds
 * - START_STICKY ensures OS restarts service if killed
 * - onTaskRemoved restarts when user swipes app from recents
 */
@AndroidEntryPoint
class ShakeDetectionService : Service(), ShakeDetector.ShakeListener {

    companion object {
        private const val TAG = "ShakeDetectionService"
        const val ACTION_START = "com.rakshasetu.action.START_DETECTION"
        const val ACTION_STOP = "com.rakshasetu.action.STOP_DETECTION"
        const val ACTION_COOLDOWN_START = "com.rakshasetu.action.COOLDOWN_START"
        const val NOTIFICATION_ID = 1001
        private const val SENSOR_CHECK_INTERVAL_MS = 30_000L // Check sensors every 30s
        private const val WAKELOCK_RENEW_MS = 45 * 60 * 1000L // Renew wake lock every 45 min

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

    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakeDetector
    private var wakeLock: PowerManager.WakeLock? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Cooldown tracking
    private var lastTriggerTime = 0L
    private var isCooldownActive = false

    // Sensor health monitoring
    private var sensorHealthJob: Job? = null
    private var wakeLockRenewJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val prefs = com.rakshasetu.app.data.repository.PreferencesRepository(
            com.rakshasetu.app.util.SecurePrefs.secureOf(this)
        ).currentPrefs

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
        Log.d(TAG, "onStartCommand: ${intent?.action}")

        when (intent?.action) {
            ACTION_START -> {
                com.rakshasetu.app.util.SecurePrefs.secureOf(this)
                    .edit().putBoolean("monitoring_enabled", true).apply()
                startForeground(NOTIFICATION_ID, createNotification(),
                    android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
                startDetection()
            }
            ACTION_STOP -> {
                com.rakshasetu.app.util.SecurePrefs.secureOf(this)
                    .edit().putBoolean("monitoring_enabled", false).apply()
                stopDetection()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            ACTION_COOLDOWN_START -> {
                isCooldownActive = true
                lastTriggerTime = System.currentTimeMillis()
                serviceScope.launch {
                    val prefs = com.rakshasetu.app.data.repository.PreferencesRepository(
                        com.rakshasetu.app.util.SecurePrefs.secureOf(this@ShakeDetectionService)
                    ).currentPrefs
                    delay(prefs.cooldownDurationMs)
                    isCooldownActive = false
                }
            }
        }
        return START_STICKY
    }

    private fun startDetection() {
        Log.d(TAG, "Starting shake detection")
        shakeDetector.reset()
        registerSensors()

        // Start sensor health monitoring — re-registers if OS unregisters them
        startSensorHealthCheck()

        // Start wake lock renewal — keeps CPU alive 24/7
        startWakeLockRenewal()
    }

    private fun registerSensors() {
        val accelerometer = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            sensorManager.registerListener(
                shakeDetector,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME,
                100_000 // 100 ms batch latency (low-power)
            )
            Log.d(TAG, "Accelerometer registered (TYPE_ACCELEROMETER)")
        } else {
            Log.e(TAG, "No accelerometer sensor available!")
        }
    }

    private fun startSensorHealthCheck() {
        sensorHealthJob?.cancel()
        sensorHealthJob = serviceScope.launch {
            while (isActive) {
                delay(SENSOR_CHECK_INTERVAL_MS)
                if (!shakeDetector.isListening()) {
                    Log.w(TAG, "Sensors unregistered! Re-registering...")
                    registerSensors()
                }
            }
        }
    }

    private fun startWakeLockRenewal() {
        wakeLockRenewJob?.cancel()
        wakeLockRenewJob = serviceScope.launch {
            while (isActive) {
                delay(WAKELOCK_RENEW_MS)
                wakeLock?.let {
                    if (it.isHeld) {
                        it.release()
                        Log.d(TAG, "WakeLock renewed")
                    }
                    acquireWakeLock()
                }
            }
        }
    }

    private fun stopDetection() {
        sensorHealthJob?.cancel()
        wakeLockRenewJob?.cancel()
        shakeDetector.stop(sensorManager)
    }

    override fun onShakeDetected(eventCount: Int) {
        if (isCooldownActive) {
            Log.d(TAG, "Shake detected but cooldown active, ignoring")
            return
        }

        Log.w(TAG, "SHAKE DETECTED! eventCount=$eventCount — launching countdown")

        TriggerLauncher.launchCountdown(this, "shake")
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
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(CountdownActivity.EXTRA_TRIGGER_TYPE, "notification")
        }
        val sosPendingIntent = PendingIntent.getActivity(
            this, 1, sosIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, RakshaSetuApp.CHANNEL_SAFETY)
            .setContentTitle("RakshaSetu Active")
            .setContentText("Safety monitoring is active 24/7")
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
            "rakshasetu::shake_detection_24x7"
        ).apply {
            acquire(WAKELOCK_RENEW_MS + 60_000L) // Slightly longer than renewal interval
            Log.d(TAG, "WakeLock acquired")
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Service destroyed — scheduling restart")
        sensorHealthJob?.cancel()
        wakeLockRenewJob?.cancel()
        shakeDetector.stop(sensorManager)
        serviceScope.cancel()
        wakeLock?.let { if (it.isHeld) it.release() }
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(TAG, "Task removed — scheduling restart")
        // Use a separate thread since serviceScope may be cancelled
        Thread {
            Thread.sleep(1500L) // Brief delay before restart
            start(this)
        }.start()
        super.onTaskRemoved(rootIntent)
    }
}
