package com.rakshasetu.app.ui.countdown

import org.junit.Assert.assertEquals
import org.junit.Test

class CountdownPolicyTest {

    @Test
    fun `visible countdown vibrates and flashes, not dimmed`() {
        val b = CountdownPolicy.behavior(isSilent = false)
        assertEquals(true, b.vibrate)
        assertEquals(true, b.flashScreen)
        assertEquals(false, b.dimScreen)
    }

    @Test
    fun `silent countdown is quiet and dimmed`() {
        val b = CountdownPolicy.behavior(isSilent = true)
        assertEquals(false, b.vibrate)
        assertEquals(false, b.flashScreen)
        assertEquals(true, b.dimScreen)
    }

    @Test
    fun `policy exposes countdown flag semantics`() {
        assertEquals(true, CountdownPolicy.behavior(false).showCountdownUi)
        assertEquals(true, CountdownPolicy.behavior(true).showCountdownUi)
    }
}
