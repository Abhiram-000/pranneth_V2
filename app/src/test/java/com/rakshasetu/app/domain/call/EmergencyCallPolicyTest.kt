package com.rakshasetu.app.domain.call

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EmergencyCallPolicyTest {

    @Test
    fun `missed calls hang up after a few rings`() {
        assertEquals(4000L, EmergencyCallPolicy.hangUpAfterMs(peerIsEmergencyOperator = false))
    }

    @Test
    fun `emergency call never force-hangs up`() {
        assertNull(EmergencyCallPolicy.hangUpAfterMs(peerIsEmergencyOperator = true))
    }
}
