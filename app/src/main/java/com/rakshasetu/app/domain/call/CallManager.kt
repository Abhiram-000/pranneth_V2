package com.rakshasetu.app.domain.call

import android.annotation.SuppressLint
import android.app.Activity
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
     * Places a call to a contact. Rings for [ringDurationMs] then hangs up.
     */
    @SuppressLint("MissingPermission")
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

            handler.postDelayed({ endCall() }, ringDurationMs)
        } catch (e: Exception) {
            Log.e("CallManager", "Failed to place call", e)
            callback?.onCallFailed(contact.id, e.message ?: "Unknown error")
        }
    }

    /**
     * Places a call to the emergency number (112).
     */
    @SuppressLint("MissingPermission")
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

            handler.postDelayed({ endCall() }, ringDurationMs)
        } catch (e: Exception) {
            Log.e("CallManager", "Failed to place emergency call", e)
            callback?.onCallFailed(-1L, e.message ?: "Unknown error")
        }
    }

    /**
     * Ends the current call.
     */
    @SuppressLint("MissingPermission")
    fun endCall() {
        val contactId = currentCallContactId ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
                telecomManager?.endCall()
            } else {
                @Suppress("DEPRECATION")
                val telecomManager = context.getSystemService(Context.TELECOM_SERVICE)
                val endCallMethod = telecomManager?.javaClass?.getMethod("endCall")
                endCallMethod?.invoke(telecomManager)
            }
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
