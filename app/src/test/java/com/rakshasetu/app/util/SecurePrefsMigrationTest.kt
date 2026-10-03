package com.rakshasetu.app.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurePrefsMigrationTest {

    @Test
    fun `migrates when plain prefs have data and secure prefs are empty`() {
        assertTrue(SecurePrefs.shouldMigrate(plainExplainsData = true, secureHasData = false))
    }

    @Test
    fun `does not migrate when secure prefs already populated`() {
        assertFalse(SecurePrefs.shouldMigrate(plainExplainsData = true, secureHasData = true))
    }

    @Test
    fun `does not migrate when nothing in plain prefs`() {
        assertFalse(SecurePrefs.shouldMigrate(plainExplainsData = false, secureHasData = false))
    }
}
