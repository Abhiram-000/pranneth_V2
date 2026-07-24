package com.rakshasetu.app.data.dao

import androidx.room.*
import com.rakshasetu.app.data.entity.EmergencyContact
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyContactDao {

    @Query("SELECT * FROM emergency_contacts ORDER BY priority ASC, createdAt ASC")
    fun getAllContacts(): Flow<List<EmergencyContact>>

    @Query("SELECT * FROM emergency_contacts ORDER BY priority ASC")
    suspend fun getAllContactsList(): List<EmergencyContact>

    @Query("SELECT * FROM emergency_contacts WHERE id = :id")
    suspend fun getContactById(id: Long): EmergencyContact?

    @Query("SELECT COUNT(*) FROM emergency_contacts")
    fun getContactCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM emergency_contacts")
    suspend fun getContactCountSync(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContact): Long

    @Update
    suspend fun updateContact(contact: EmergencyContact)

    @Delete
    suspend fun deleteContact(contact: EmergencyContact)

    @Query("DELETE FROM emergency_contacts WHERE id = :id")
    suspend fun deleteContactById(id: Long)

    @Query("UPDATE emergency_contacts SET priority = :priority WHERE id = :id")
    suspend fun updatePriority(id: Long, priority: Int)

    @Query("UPDATE emergency_contacts SET isVerified = :isVerified, lastNotifiedAt = :timestamp WHERE id = :id")
    suspend fun updateVerification(id: Long, isVerified: Boolean, timestamp: Long? = null)

    @Query("SELECT * FROM emergency_contacts WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getContactByPhone(phone: String): EmergencyContact?
}
