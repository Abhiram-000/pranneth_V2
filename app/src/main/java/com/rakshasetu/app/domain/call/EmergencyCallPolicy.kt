package com.rakshasetu.app.domain.call

/**
 * Missed calls must ring then disconnect promptly; the 112 emergency call must
 * never be auto-terminated — the user needs the call to stay live for the operator.
 */
object EmergencyCallPolicy {

    fun hangUpAfterMs(peerIsEmergencyOperator: Boolean): Long? =
        if (peerIsEmergencyOperator) null else 4000L
}
