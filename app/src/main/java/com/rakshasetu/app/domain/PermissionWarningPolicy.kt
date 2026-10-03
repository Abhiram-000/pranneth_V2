package com.rakshasetu.app.domain

/**
 * Decide every app-open whether a persistent permission warning is due:
 * silent revoke detection rather than failing on the day it is needed.
 */
object PermissionWarningPolicy {

    fun shouldWarn(missingCritical: List<String>): Boolean = missingCritical.isNotEmpty()
}
