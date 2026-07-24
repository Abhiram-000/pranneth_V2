package com.rakshasetu.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rakshasetu.app.data.entity.EmergencyContact
import com.rakshasetu.app.data.repository.ContactRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Handles SMS delivery receipts.
 * Updates the contact's last notified timestamp on successful delivery.
 */
@AndroidEntryPoint
class SmsDeliveredReceiver : BroadcastReceiver() {

    @Inject
    lateinit var contactRepository: ContactRepository

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        val contactId = intent.getLongExtra("contact_id", -1L)
        if (contactId == -1L) return

        val resultCode = resultData?.toIntOrNull() ?: return

        when (resultCode) {
            android.app.Activity.RESULT_OK -> {
                // SMS delivered successfully
                scope.launch {
                    contactRepository.markVerified(contactId)
                }
            }
            android.app.Activity.RESULT_CANCELED -> {
                // SMS delivery failed — will be handled by retry logic
            }
        }
    }
}
