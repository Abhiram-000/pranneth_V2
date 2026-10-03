package com.rakshasetu.app.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionWarningPolicyTest {

    @Test
    fun `missing any critical permission shows warning`() {
        assertTrue(PermissionWarningPolicy.shouldWarn(missingCritical = listOf("android.permission.SEND_SMS")))
    }

    @Test
    fun `nothing missing means no warning`() {
        assertFalse(PermissionWarningPolicy.shouldWarn(missingCritical = emptyList()))
    }
}
