package com.rakshasetu.app.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import com.rakshasetu.app.data.entity.EmergencyContact

/**
 * Handles sending test/verification SMS to emergency contacts.
 * The test SMS is clearly marked as TEST so contacts know it's not a real emergency.
 */
object SmsVerificationHelper {

    const val ACTION_TEST_SMS_SENT = "com.rakshasetu.action.TEST_SMS_SENT"
    const val ACTION_TEST_SMS_DELIVERED = "com.rakshasetu.action.TEST_SMS_DELIVERED"

    /**
     * Formats a test SMS message.
     */
    fun formatTestMessage(contactName: String): String {
        return "🧪 TEST ALERT from RakshaSetu\n\n" +
                "Hi $contactName! This is a test message to confirm you're registered " +
                "as an emergency contact. You do NOT need to take any action.\n\n" +
                "When a real alert is triggered, you'll receive a message like this with " +
                "the user's live location and an SOS marker.\n\n" +
                "— RakshaSetu Safety App"
    }

    /**
     * Formats a real emergency SMS message.
     */
    fun formatEmergencyMessage(
        template: String,
        latitude: Double?,
        longitude: Double?,
        accuracy: Float?,
        batteryLevel: Int?,
        hasData: Boolean,
        isBatteryLow: Boolean = false,
        provider: String? = null
    ): String {
        val locationText = if (latitude != null && longitude != null) {
            val accuracyText = if (accuracy != null && accuracy > 0) " (accurate to ~${accuracy.toInt()}m)" else ""
            val sourceText = if (provider == "network") " — network estimate" else ""
            "https://maps.google.com/?q=$latitude,$longitude$accuracyText$sourceText"
        } else {
            "Location unavailable — please call immediately"
        }

        val batteryText = buildString {
            batteryLevel?.let { append("Battery: $it%") }
            if (isBatteryLow) append(" ⚠️ LOW")
        }

        return template
            .replace("{location}", locationText)
            .replace("{battery}", batteryText)
            .replace("{data_status}", if (!hasData) "No data connection" else "")
            .replace("{timestamp}", java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                java.util.Locale.getDefault()
            ).format(java.util.Date()))
            .trim()
    }

    /**
     * Sends a test SMS to verify the contact.
     */
    @SuppressLint("MissingPermission")
    fun sendTestSms(
        context: Context,
        contact: EmergencyContact,
        requestCode: Int
    ): Boolean {
        return try {
            val smsManager = context.getSystemService(SmsManager::class.java)
                ?: SmsManager.getDefault()

            val message = formatTestMessage(contact.name)
            val sentIntent = Intent(ACTION_TEST_SMS_SENT).apply {
                putExtra("contact_id", contact.id)
                putExtra("contact_name", contact.name)
            }
            val sentPending = PendingIntent.getBroadcast(
                context,
                requestCode,
                sentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val deliveredIntent = Intent(ACTION_TEST_SMS_DELIVERED).apply {
                putExtra("contact_id", contact.id)
            }
            val deliveredPending = PendingIntent.getBroadcast(
                context,
                requestCode + 1000,
                deliveredIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val parts = smsManager.divideMessage(message)
            if (parts != null && parts.size > 1) {
                val sentIntents = parts.mapIndexed { i, _ ->
                    PendingIntent.getBroadcast(
                        context, requestCode + i, Intent(ACTION_TEST_SMS_SENT).apply {
                            putExtra("contact_id", contact.id)
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }
                smsManager.sendMultipartTextMessage(
                    contact.fullPhoneNumber, null, parts,
                    ArrayList(sentIntents), null
                )
            } else {
                smsManager.sendTextMessage(
                    contact.fullPhoneNumber, null, message,
                    sentPending, deliveredPending
                )
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Sends a real emergency SMS.
     */
    @SuppressLint("MissingPermission")
    fun sendEmergencySms(
        context: Context,
        phoneNumber: String,
        message: String,
        sentRequestCode: Int,
        deliveredRequestCode: Int
    ): Boolean {
        return try {
            val smsManager = context.getSystemService(SmsManager::class.java)
                ?: SmsManager.getDefault()

            val sentIntent = Intent("com.rakshasetu.app.SMS_SENT").apply {
                putExtra("contact_id", -1L)
            }
            val sentPending = PendingIntent.getBroadcast(
                context, sentRequestCode, sentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val deliveredIntent = Intent("com.rakshasetu.app.SMS_DELIVERED").apply {
                putExtra("contact_id", -1L)
            }
            val deliveredPending = PendingIntent.getBroadcast(
                context, deliveredRequestCode, deliveredIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val parts = smsManager.divideMessage(message)
            if (parts != null && parts.size > 1) {
                val sentIntents = parts.mapIndexed { i, _ ->
                    PendingIntent.getBroadcast(
                        context, sentRequestCode + i, Intent("com.rakshasetu.app.SMS_SENT").apply {
                            putExtra("contact_id", -1L)
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }
                smsManager.sendMultipartTextMessage(
                    phoneNumber, null, parts,
                    ArrayList(sentIntents), null
                )
            } else {
                smsManager.sendTextMessage(
                    phoneNumber, null, message,
                    sentPending, deliveredPending
                )
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
