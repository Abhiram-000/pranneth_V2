package com.rakshasetu.app.data.dao

import androidx.room.*
import com.rakshasetu.app.data.entity.AlertLog
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertLogDao {

    @Query("SELECT * FROM alert_logs ORDER BY startedAt DESC LIMIT :limit")
    fun getRecentAlerts(limit: Int = 50): Flow<List<AlertLog>>

    @Query("SELECT * FROM alert_logs ORDER BY startedAt DESC LIMIT :limit")
    suspend fun getRecentAlertsList(limit: Int = 50): List<AlertLog>

    @Query("SELECT * FROM alert_logs WHERE id = :id")
    suspend fun getAlertById(id: Long): AlertLog?

    @Query("SELECT * FROM alert_logs WHERE endedAt IS NULL ORDER BY startedAt DESC LIMIT 1")
    suspend fun getActiveAlert(): AlertLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertLog): Long

    @Update
    suspend fun updateAlert(alert: AlertLog)

    @Query("UPDATE alert_logs SET endedAt = :endedAt, isCancelled = :isCancelled, cancelMethod = :cancelMethod WHERE id = :id")
    suspend fun endAlert(id: Long, endedAt: Long, isCancelled: Boolean = false, cancelMethod: String? = null)

    @Query("UPDATE alert_logs SET smsSentCount = smsSentCount + 1 WHERE id = :id")
    suspend fun incrementSmsCount(id: Long)

    @Query("UPDATE alert_logs SET callsMadeCount = callsMadeCount + 1 WHERE id = :id")
    suspend fun incrementCallCount(id: Long)

    @Query("UPDATE alert_logs SET isEscalated = 1 WHERE id = :id")
    suspend fun markEscalated(id: Long)

    @Query("SELECT * FROM alert_logs WHERE startedAt > :since ORDER BY startedAt DESC")
    suspend fun getAlertsSince(since: Long): List<AlertLog>

    @Query("DELETE FROM alert_logs WHERE startedAt < :before")
    suspend fun deleteAlertsOlderThan(before: Long)
}
