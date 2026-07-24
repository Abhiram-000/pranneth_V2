package com.rakshasetu.app.domain.call

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telecom.TelecomManager
import com.rakshasetu.app.data.entity.EmergencyContact
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles placing calls to emergency contacts and 112.
 * Implements the "missed call" pattern — ring for a few seconds then hang up
 * so the contact gets a notification to check their SMS.
 */
@Singleton
class CallManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    interface CallCallback {
        fun onCallStarted(contactId: Long)
        fun onCallEnded(contactId: Long)
        fun onCallFailed(contactId: Long, error: String)
    }

    private val handler = Handler(Looper.getMainLooper())
    private var callback: CallCallback? = null
    private var currentCallContactId: Long? = null

    fun setCallback(callback: CallCallback) {
        this.callback = callback
    }

    /**
     * Places a call to a contact. Rings for [ringDurationMs] then hangs up
     * to create a missed-call notification.
     */
    fun placeMissedCall(
        contact: EmergencyContact,
        ringDurationMs: Long = 5000L
    ) {
        currentCallContactId = contact.id
        callback?.onCallStarted(contact.id)

        try {
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:${contact.fullPhoneNumber}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(callIntent)

            // Hang up after ring duration
            handler.postDelayed({
                endCall()
            }, ringDurationMs)
        } catch (e: Exception) {
            callback?.onCallFailed(contact.id, e.message ?: "Unknown error")
        }
    }

    /**
     * Places a call to the emergency number (112).
     */
    fun placeEmergencyCall(
        emergencyNumber: String,
        ringDurationMs: Long = 8000L
    ) {
        currentCallContactId = -1L
        callback?.onCallStarted(-1L)

        try {
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$emergencyNumber")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(callIntent)

            handler.postDelayed({
                endCall()
            }, ringDurationMs)
        } catch (e: Exception) {
            callback?.onCallFailed(-1L, e.message ?: "Unknown error")
        }
    }

    /**
     * Ends the current call using TelecomManager where available.
     */
    fun endCall() {
        val contactId = currentCallContactId ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
                telecomManager?.endCall()
            } else {
                // Fallback: try to end via reflection (not guaranteed on all devices)
                try {
                    val telecomManager = context.getSystemService(Context.TELECOM_SERVICE)
                    val endCallMethod = telecomManager?.javaClass?.getMethod("endCall")
                    endCallMethod?.invoke(telecomManager)
                } catch (_: Exception) {
                    // Best effort — call may continue ringing on some devices
                }
            }
        } catch (e: Exception) {
            // Best effort
        }
        callback?.onCallEnded(contactId)
        currentCallContactId = null
    }

    fun cancelPendingHangup() {
        handler.removeCallbacksAndMessages(null)
    }
}
