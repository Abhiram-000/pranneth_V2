package com.rakshasetu.app

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

import com.rakshasetu.app.worker.WatchdogScheduler

@HiltAndroidApp
class RakshaSetuApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        WatchdogScheduler.schedule(this)
    }

    private fun createNotificationChannels() {
        val notificationManager = getSystemService(NotificationManager::class.java)

        // Safety monitoring foreground service channel
        val safetyChannel = NotificationChannel(
            CHANNEL_SAFETY,
            "Safety Monitoring",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows when safety monitoring is active"
            setShowBadge(false)
            enableVibration(false)
        }

        // Emergency alert channel (high priority for countdown)
        val alertChannel = NotificationChannel(
            CHANNEL_ALERT,
            "Emergency Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Emergency alert notifications"
            setShowBadge(true)
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        // SOS quick action channel (lock screen)
        val sosChannel = NotificationChannel(
            CHANNEL_SOS,
            "SOS Quick Action",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Quick SOS trigger from notification"
            setShowBadge(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        notificationManager.createNotificationChannel(safetyChannel)
        notificationManager.createNotificationChannel(alertChannel)
        notificationManager.createNotificationChannel(sosChannel)
    }

    companion object {
        const val CHANNEL_SAFETY = "safety_monitoring"
        const val CHANNEL_ALERT = "emergency_alerts"
        const val CHANNEL_SOS = "sos_quick_action"
    }
}
