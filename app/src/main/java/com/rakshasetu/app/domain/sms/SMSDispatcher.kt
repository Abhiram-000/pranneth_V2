package com.rakshasetu.app.domain.sms

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsManager
import com.rakshasetu.app.data.entity.EmergencyContact
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles sending SMS messages to emergency contacts and 112.
 * Supports multi-part SMS for long messages (important for regional languages).
 * Handles delivery receipts and retry logic.
 */
@Singleton
class SMSDispatcher @Inject constructor(
    @ApplicationContext private val context: Context
) {
    interface SmsCallback {
        fun onSmsSent(contactId: Long, success: Boolean)
        fun onSmsDelivered(contactId: Long, success: Boolean)
    }

    /**
     * Formats the SMS template with actual location and context data.
     */
    fun formatSmsMessage(
        template: String,
        latitude: Double?,
        longitude: Double?,
        accuracy: Float?,
        batteryLevel: Int?,
        hasData: Boolean
    ): String {
        val locationText = if (latitude != null && longitude != null) {
            val accuracyText = if (accuracy != null) " (~${accuracy.toInt()}m accuracy)" else ""
            "https://maps.google.com/?q=$latitude,$longitude$accuracyText"
        } else {
            "Location unavailable"
        }

        val batteryText = if (batteryLevel != null) "Battery: $batteryLevel%" else ""
        val dataText = if (!hasData) "No data connection" else ""

        return template
            .replace("{location}", locationText)
            .replace("{battery}", batteryText)
            .replace("{data_status}", dataText)
            .replace("{timestamp}", java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                java.util.Locale.getDefault()
            ).format(java.util.Date()))
    }

    /**
     * Sends SMS to a contact. Returns true if the send was initiated successfully.
     * Note: actual delivery is async via the sent/delivery intents.
     */
    fun sendSms(
        contact: EmergencyContact,
        message: String,
        sentRequestCode: Int,
        deliveredRequestCode: Int
    ): Boolean {
        return try {
            val smsManager = SmsManager.getDefault()

            // Check if message needs to be split (multi-part)
            val parts = smsManager.divideMessage(message)

            if (parts != null && parts.size > 1) {
                // Multi-part SMS
                val sentIntents = parts.mapIndexed { index, _ ->
                    val sentIntent = Intent("com.rakshasetu.app.SMS_SENT")
                    sentIntent.putExtra("contact_id", contact.id)
                    sentIntent.putExtra("part_index", index)
                    PendingIntent.getBroadcast(
                        context,
                        sentRequestCode + index,
                        sentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }
                val deliveredIntents = parts.mapIndexed { index, _ ->
                    val deliveredIntent = Intent("com.rakshasetu.app.SMS_DELIVERED")
                    deliveredIntent.putExtra("contact_id", contact.id)
                    deliveredIntent.putExtra("part_index", index)
                    PendingIntent.getBroadcast(
                        context,
                        deliveredRequestCode + index,
                        deliveredIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }

                smsManager.sendMultipartTextMessage(
                    contact.fullPhoneNumber,
                    null,
                    parts,
                    sentIntents.toArrayList(),
                    deliveredIntents.toArrayList()
                )
            } else {
                // Single-part SMS
                val sentIntent = Intent("com.rakshasetu.app.SMS_SENT")
                sentIntent.putExtra("contact_id", contact.id)
                val sentPendingIntent = PendingIntent.getBroadcast(
                    context,
                    sentRequestCode,
                    sentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val deliveredIntent = Intent("com.rakshasetu.app.SMS_DELIVERED")
                deliveredIntent.putExtra("contact_id", contact.id)
                val deliveredPendingIntent = PendingIntent.getBroadcast(
                    context,
                    deliveredRequestCode,
                    deliveredIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                smsManager.sendTextMessage(
                    contact.fullPhoneNumber,
                    null,
                    message,
                    sentPendingIntent,
                    deliveredPendingIntent
                )
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Sends SMS to the emergency number (112/ERSS).
     */
    fun sendEmergencySms(
        emergencyNumber: String,
        message: String,
        sentRequestCode: Int
    ): Boolean {
        return try {
            val smsManager = SmsManager.getDefault()
            val sentIntent = Intent("com.rakshasetu.app.SMS_SENT")
            sentIntent.putExtra("contact_id", -1L) // -1 for emergency number
            val sentPendingIntent = PendingIntent.getBroadcast(
                context,
                sentRequestCode,
                sentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            smsManager.sendTextMessage(
                emergencyNumber,
                null,
                message,
                sentPendingIntent,
                null
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun <T> List<T>.toArrayList(): ArrayList<T> = ArrayList(this)
}
