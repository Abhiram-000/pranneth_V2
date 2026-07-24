package com.rakshasetu.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rakshasetu.app.data.repository.AlertRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Handles SMS sent receipts.
 * Tracks successful/failed sends for retry logic.
 */
@AndroidEntryPoint
class SmsSentReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alertRepository: AlertRepository

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        val contactId = intent.getLongExtra("contact_id", -1L)
        val resultCode = resultData?.toIntOrNull() ?: return

        when (resultCode) {
            android.app.Activity.RESULT_OK -> {
                // SMS sent successfully
                scope.launch {
                    // Find active alert and increment count
                    val activeAlert = alertRepository.getActiveAlert()
                    activeAlert?.let {
                        alertRepository.incrementSmsCount(it.id)
                    }
                }
            }
            android.app.Activity.RESULT_CANCELED -> {
                // SMS send failed — retry logic will be handled by AlertDispatchService
            }
        }
    }
}
