package com.rakshasetu.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rakshasetu.app.R
import com.rakshasetu.app.RakshaSetuApp
import com.rakshasetu.app.service.ShakeDetectionService
import com.rakshasetu.app.util.OEMHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Periodic safety-net: if the monitoring foreground service has died (OEM
 * process killer, force-stop) while the user still wants protection, restart
 * it and surface a notification so the user knows monitoring resumed.
 */
@HiltWorker
class ServiceWatchdogWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val monitoringEnabled = try {
            applicationContext.getSharedPreferences("rakshasetu_prefs", Context.MODE_PRIVATE)
                .getBoolean("monitoring_enabled", true)
        } catch (_: Exception) { true }

        val running = OEMHelper.isServiceRunning(applicationContext, ShakeDetectionService::class.java)
        Log.i(TAG, "watchdog tick: running=$running monitoringEnabled=$monitoringEnabled")

        if (WatchdogPolicy.shouldRestart(running, monitoringEnabled)) {
            try {
                applicationContext.startForegroundService(
                    Intent(applicationContext, ShakeDetectionService::class.java)
                        .setAction(ShakeDetectionService.ACTION_START)
                )
                notifyRestarted()
            } catch (e: Exception) {
                Log.e(TAG, "watchdog failed to restart service", e)
                return Result.retry()
            }
        }
        return Result.success()
    }

    private fun notifyRestarted() {
        val nm = applicationContext.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(CHANNEL, "Watchdog", NotificationManager.IMPORTANCE_HIGH)
        nm.createNotificationChannel(channel)
        nm.notify(9001, NotificationCompat.Builder(applicationContext, CHANNEL)
            .setSmallIcon(R.drawable.ic_shield)
            .setContentTitle("RakshaSetu monitoring restarted")
            .setContentText("Background protection had stopped and was restarted by the watchdog.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build())
    }

    companion object {
        private const val TAG = "ServiceWatchdog"
        const val CHANNEL = "watchdog"
    }
}
