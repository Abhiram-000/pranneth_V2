package com.rakshasetu.app.data.repository

import com.rakshasetu.app.data.dao.EmergencyContactDao
import com.rakshasetu.app.data.entity.EmergencyContact
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepository @Inject constructor(
    private val contactDao: EmergencyContactDao
) {
    fun getAllContacts(): Flow<List<EmergencyContact>> = contactDao.getAllContacts()

    suspend fun getAllContactsList(): List<EmergencyContact> = contactDao.getAllContactsList()

    suspend fun getContactById(id: Long): EmergencyContact? = contactDao.getContactById(id)

    fun getContactCount(): Flow<Int> = contactDao.getContactCount()

    suspend fun getContactCountSync(): Int = contactDao.getContactCountSync()

    suspend fun addContact(contact: EmergencyContact): Long = contactDao.insertContact(contact)

    suspend fun updateContact(contact: EmergencyContact) = contactDao.updateContact(contact)

    suspend fun deleteContact(contact: EmergencyContact) = contactDao.deleteContact(contact)

    suspend fun deleteContactById(id: Long) = contactDao.deleteContactById(id)

    suspend fun updatePriority(id: Long, priority: Int) = contactDao.updatePriority(id, priority)

    suspend fun markVerified(id: Long) = contactDao.updateVerification(id, true, System.currentTimeMillis())

    suspend fun reorderContacts(contacts: List<EmergencyContact>) {
        contacts.forEachIndexed { index, contact ->
            contactDao.updatePriority(contact.id, index)
        }
    }
}
