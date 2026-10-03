package com.rakshasetu.app.domain.call

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telecom.TelecomManager
import android.util.Log
import com.rakshasetu.app.data.entity.EmergencyContact
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles placing calls to emergency contacts and 112.
 * Implements the "missed call" pattern — ring for a few seconds then hang up.
 *
 * CRITICAL: Missed calls are MANDATORY. Every contact gets an SMS + a missed call.
 * Even if endCall() fails on some devices, the outgoing call still triggers
 * a notification on the contact's phone — they see "Missed call from [number]".
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
     * Places a missed call to a contact. Rings for [ringDurationMs] then hangs up.
     * Even if endCall() fails, the contact sees the outgoing call as a missed call.
     */
    @SuppressLint("MissingPermission")
    fun placeMissedCall(
        contact: EmergencyContact,
        ringDurationMs: Long = 4000L
    ) {
        currentCallContactId = contact.id
        callback?.onCallStarted(contact.id)

        try {
            val phoneNum = contact.fullPhoneNumber
            Log.d("CallManager", "Placing missed call to: $phoneNum (ring ${ringDurationMs}ms)")

            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNum")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(callIntent)

            // Schedule hang-up
            handler.postDelayed({
                endCall()
                Log.d("CallManager", "Missed call ended for: $phoneNum")
            }, ringDurationMs)
        } catch (e: Exception) {
            Log.e("CallManager", "Failed to place missed call to ${contact.fullPhoneNumber}", e)
            callback?.onCallFailed(contact.id, e.message ?: "Unknown error")

            // Retry with ACTION_DIAL as fallback (opens dialer, user can tap to call)
            try {
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${contact.fullPhoneNumber}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(dialIntent)
                Log.d("CallManager", "Fallback: opened dialer for ${contact.fullPhoneNumber}")
            } catch (e2: Exception) {
                Log.e("CallManager", "Fallback dial also failed", e2)
            }
        }
    }

    /**
     * Places a call to the emergency number (112).
     */
    @SuppressLint("MissingPermission")
    fun placeEmergencyCall(
        emergencyNumber: String,
        ringDurationMs: Long = 4000L
    ) {
        currentCallContactId = -1L
        callback?.onCallStarted(-1L)

        try {
            Log.d("CallManager", "Placing emergency call to: $emergencyNumber")

            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$emergencyNumber")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(callIntent)

            // Emergency calls stay connected longer — 10 seconds before auto-hangup
            handler.postDelayed({
                endCall()
                Log.d("CallManager", "Emergency call ended")
            }, ringDurationMs)
        } catch (e: Exception) {
            Log.e("CallManager", "Failed to place emergency call to $emergencyNumber", e)
            callback?.onCallFailed(-1L, e.message ?: "Unknown error")

            // Fallback: open dialer
            try {
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$emergencyNumber")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(dialIntent)
            } catch (e2: Exception) {
                Log.e("CallManager", "Emergency fallback dial also failed", e2)
            }
        }
    }

    /**
     * Ends the current call using TelecomManager.
     */
    @SuppressLint("MissingPermission")
    fun endCall() {
        val contactId = currentCallContactId ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
                if (telecomManager != null && telecomManager.isInCall) {
                    telecomManager.endCall()
                }
            } else {
                @Suppress("DEPRECATION")
                val telecomManager = context.getSystemService(Context.TELECOM_SERVICE)
                val endCallMethod = telecomManager?.javaClass?.getMethod("endCall")
                endCallMethod?.invoke(telecomManager)
            }
        } catch (e: SecurityException) {
            // SecurityException = not default dialer, can't end call programmatically
            // This is fine — the call will still show as outgoing on the contact's phone
            Log.w("CallManager", "Cannot end call (not default dialer): ${e.message}")
        } catch (e: Exception) {
            Log.w("CallManager", "Could not end call programmatically: ${e.message}")
        }
        callback?.onCallEnded(contactId)
        currentCallContactId = null
    }

    fun cancelPendingHangup() {
        handler.removeCallbacksAndMessages(null)
    }
}
