package com.rakshasetu.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import com.rakshasetu.app.data.entity.AlertLog
import com.rakshasetu.app.data.repository.AlertRepository
import com.rakshasetu.app.util.OEMHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Detects airplane mode changes and fires an alert with last known location
 * before connectivity is completely lost.
 */
@AndroidEntryPoint
class AirplaneModeReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alertRepository: AlertRepository

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            val isAirplaneModeOn = intent.getBooleanExtra("state", false)

            if (isAirplaneModeOn) {
                // Airplane mode just turned ON — fire immediate alert
                // with last known location before connectivity is lost
                scope.launch {
                    val activeAlert = alertRepository.getActiveAlert()
                    if (activeAlert == null) {
                        // No active alert — create one
                        val alert = AlertLog(
                            triggerType = AlertLog.TRIGGER_AIRPLANE,
                            hasDataConnection = false,
                            batteryLevel = OEMHelper.getBatteryLevel(context)
                        )
                        val alertId = alertRepository.createAlert(alert)

                        // Start alert dispatch
                        val dispatchIntent = Intent(context, AlertDispatchService::class.java).apply {
                            action = AlertDispatchService.ACTION_DISPATCH_ALERT
                            putExtra(AlertDispatchService.EXTRA_ALERT_ID, alertId)
                            putExtra(AlertDispatchService.EXTRA_IS_AIRPLANE_MODE, true)
                        }
                        context.startForegroundService(dispatchIntent)
                    }
                    // If there's already an active alert, it will continue with degraded connectivity
                }
            }
        }
    }
}
