package com.rakshasetu.app.domain.escalation

import com.rakshasetu.app.data.entity.EmergencyContact
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages escalation logic — if a primary contact doesn't acknowledge
 * within a timeout, automatically escalate to the next contact in priority order.
 *
 * Escalation flow:
 * 1. Alert top-priority contacts first
 * 2. Wait for acknowledgement window
 * 3. If no acknowledgement, escalate to next priority
 * 4. Continue until all contacts notified or alert is cancelled
 */
@Singleton
class EscalationManager @Inject constructor() {

    interface EscalationCallback {
        fun onEscalateToContact(contact: EmergencyContact, escalationLevel: Int)
        fun onAllContactsNotified()
        fun onEscalationComplete()
    }

    private var callback: EscalationCallback? = null
    private var isEscalating = false
    private var currentLevel = 0
    private var contacts: List<EmergencyContact> = emptyList()

    // Time to wait for acknowledgement before escalating (ms)
    private val acknowledgementTimeoutMs = 30_000L // 30 seconds

    // Track which contacts have been notified
    private val notifiedContacts = mutableSetOf<Long>()

    fun setCallback(callback: EscalationCallback) {
        this.callback = callback
    }

    /**
     * Starts escalation with the given contacts (sorted by priority).
     */
    fun startEscalation(sortedContacts: List<EmergencyContact>) {
        contacts = sortedContacts
        currentLevel = 0
        notifiedContacts.clear()
        isEscalating = true
    }

    /**
     * Returns the next contact to notify based on escalation level.
     * Returns null if all contacts have been notified.
     */
    fun getNextContact(): EmergencyContact? {
        if (contacts.isEmpty()) return null

        // Find the next unnotified contact in priority order
        for (contact in contacts) {
            if (contact.id !in notifiedContacts) {
                return contact
            }
        }
        return null
    }

    /**
     * Marks a contact as notified and waits for acknowledgement timeout.
     * If no acknowledgement, triggers escalation to next contact.
     */
    suspend fun markNotifiedAndEscalate(contact: EmergencyContact) {
        notifiedContacts.add(contact.id)
        currentLevel++

        callback?.onEscalateToContact(contact, currentLevel)

        // Wait for acknowledgement
        delay(acknowledgementTimeoutMs)

        // If still escalating, notify next contact
        if (isEscalating) {
            val nextContact = getNextContact()
            if (nextContact != null) {
                callback?.onEscalateToContact(nextContact, currentLevel)
            } else {
                callback?.onAllContactsNotified()
                isEscalating = false
                callback?.onEscalationComplete()
            }
        }
    }

    /**
     * Stops escalation (when alert is cancelled).
     */
    fun stopEscalation() {
        isEscalating = false
        currentLevel = 0
        notifiedContacts.clear()
    }

    /**
     * Checks if a specific contact has already been notified.
     */
    fun isNotified(contactId: Long): Boolean = contactId in notifiedContacts

    /**
     * Returns the number of contacts notified so far.
     */
    fun getNotifiedCount(): Int = notifiedContacts.size

    /**
     * Returns whether escalation is still active.
     */
    fun isActive(): Boolean = isEscalating
}
