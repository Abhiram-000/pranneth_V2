package com.rakshasetu.app.domain.sms

import android.annotation.SuppressLint
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
    private fun getSmsManager(): SmsManager {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }
    }

    /**
     * Sends SMS to a contact. Returns true if the send was initiated successfully.
     */
    @SuppressLint("MissingPermission")
    fun sendSms(
        contact: EmergencyContact,
        message: String,
        sentRequestCode: Int,
        deliveredRequestCode: Int
    ): Boolean {
        return try {
            val smsManager = getSmsManager()
            val parts = smsManager.divideMessage(message)

            if (parts != null && parts.size > 1) {
                val sentIntents = parts.mapIndexed { index, _ ->
                    val sentIntent = Intent("com.rakshasetu.app.SMS_SENT")
                    sentIntent.putExtra("contact_id", contact.id)
                    PendingIntent.getBroadcast(
                        context,
                        sentRequestCode + index,
                        sentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }

                smsManager.sendMultipartTextMessage(
                    contact.fullPhoneNumber,
                    null,
                    parts,
                    ArrayList(sentIntents),
                    null
                )
            } else {
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
    @SuppressLint("MissingPermission")
    fun sendEmergencySms(
        emergencyNumber: String,
        message: String,
        sentRequestCode: Int
    ): Boolean {
        return try {
            val smsManager = getSmsManager()
            val sentIntent = Intent("com.rakshasetu.app.SMS_SENT")
            sentIntent.putExtra("contact_id", -1L)
            val sentPendingIntent = PendingIntent.getBroadcast(
                context,
                sentRequestCode,
                sentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val parts = smsManager.divideMessage(message)
            if (parts != null && parts.size > 1) {
                val sentIntents = parts.mapIndexed { index, _ ->
                    PendingIntent.getBroadcast(
                        context,
                        sentRequestCode + index,
                        Intent("com.rakshasetu.app.SMS_SENT").apply {
                            putExtra("contact_id", -1L)
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }
                smsManager.sendMultipartTextMessage(
                    emergencyNumber,
                    null,
                    parts,
                    ArrayList(sentIntents),
                    null
                )
            } else {
                smsManager.sendTextMessage(
                    emergencyNumber,
                    null,
                    message,
                    sentPendingIntent,
                    null
                )
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
