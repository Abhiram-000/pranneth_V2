package com.rakshasetu.app.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.rakshasetu.app.R
import com.rakshasetu.app.RakshaSetuApp
import com.rakshasetu.app.ui.countdown.CountdownActivity

/**
 * Launches the countdown screen from background contexts (sensor service,
 * accessibility service, notification action).
 *
 * Android 12+ blocks background activity starts, and Android 14+ enforces it
 * harder still. When the direct launch fails, we fall back to a high-priority
 * full-screen-intent notification — the OS-sanctioned path that also wakes and
 * surfaces on the lock screen.
 */
object TriggerLauncher {

    private const val TAG = "TriggerLauncher"
    private const val FALLBACK_NOTIFICATION_ID = 7100

    /** Pure decision the unit test pins. */
    fun fallbackNeeded(thrown: Throwable?, sdkInt: Int): Boolean = thrown != null

    fun launchCountdown(context: Context, triggerType: String, isSilent: Boolean = false) {
        var failure: Throwable? = null
        try {
            val intent = Intent(context, CountdownActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                putExtra(CountdownActivity.EXTRA_TRIGGER_TYPE, triggerType)
                putExtra(CountdownActivity.EXTRA_IS_SILENT, isSilent)
            }
            context.startActivity(intent)
        } catch (t: Throwable) {
            failure = t
            Log.w(TAG, "Direct countdown launch blocked: ${t.message}")
        }

        if (fallbackNeeded(failure, android.os.Build.VERSION.SDK_INT)) {
            postFullScreenFallback(context, triggerType, isSilent)
        }
    }

    private fun postFullScreenFallback(context: Context, triggerType: String, isSilent: Boolean) {
        val intent = Intent(context, CountdownActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            putExtra(CountdownActivity.EXTRA_TRIGGER_TYPE, triggerType)
            putExtra(CountdownActivity.EXTRA_IS_SILENT, isSilent)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, FALLBACK_NOTIFICATION_ID, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, RakshaSetuApp.CHANNEL_SOS)
            .setSmallIcon(R.drawable.ic_sos)
            .setContentTitle("SOS triggered")
            .setContentText("Tap to open the safety countdown")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
            .build()
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(FALLBACK_NOTIFICATION_ID, notification)
    }
}
