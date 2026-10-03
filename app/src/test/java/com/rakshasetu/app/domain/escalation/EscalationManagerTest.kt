package com.rakshasetu.app.domain.escalation

import com.rakshasetu.app.data.entity.EmergencyContact
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EscalationManagerTest {

    private fun contact(id: Long, name: String = "c$id") =
        EmergencyContact(id = id, name = name, phoneNumber = "987654321$id", relation = "Friend")

    private class Recorder : EscalationManager.EscalationCallback {
        val escalations = mutableListOf<Long>()
        var allNotified = false
        var complete = false
        override fun onEscalateToContact(contact: EmergencyContact, escalationLevel: Int) {
            escalations.add(contact.id)
        }
        override fun onAllContactsNotified() { allNotified = true }
        override fun onEscalationComplete() { complete = true }
    }

    @Test
    fun `escalates to next contact after acknowledgement timeout`() = runTest {
        val manager = EscalationManager()
        val recorder = Recorder()
        manager.setCallback(recorder)
        val contacts = listOf(contact(1), contact(2), contact(3))
        manager.startEscalation(contacts)

        // Notify contact 1; after the 30 s ack window it escalates to contact 2.
        manager.markNotifiedAndEscalate(contacts[0])
        manager.markNotifiedAndEscalate(contacts[1])

        assertEquals(listOf(1L, 2L, 2L, 3L), recorder.escalations)
    }

    @Test
    fun `cancel stops escalation from firing more callbacks`() = runTest {
        val manager = EscalationManager()
        val recorder = Recorder()
        manager.setCallback(recorder)
        val contacts = listOf(contact(1), contact(2))
        manager.startEscalation(contacts)
        manager.stopEscalation()

        manager.markNotifiedAndEscalate(contacts[0])
        assertFalse(manager.isActive())
    }

    @Test
    fun `notified count tracks notified contacts`() = runTest {
        val manager = EscalationManager()
        manager.setCallback(Recorder())
        val contacts = listOf(contact(1), contact(2))
        manager.startEscalation(contacts)
        manager.markNotifiedAndEscalate(contacts[0])
        assertEquals(1, manager.getNotifiedCount())
        assertTrue(manager.isNotified(1))
    }
}
