package com.rakshasetu.app.data.repository

import com.rakshasetu.app.data.dao.AlertLogDao
import com.rakshasetu.app.data.entity.AlertLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertRepository @Inject constructor(
    private val alertLogDao: AlertLogDao
) {
    fun getRecentAlerts(limit: Int = 50): Flow<List<AlertLog>> =
        alertLogDao.getRecentAlerts(limit)

    suspend fun getRecentAlertsList(limit: Int = 50): List<AlertLog> =
        alertLogDao.getRecentAlertsList(limit)

    suspend fun getAlertById(id: Long): AlertLog? = alertLogDao.getAlertById(id)

    suspend fun getActiveAlert(): AlertLog? = alertLogDao.getActiveAlert()

    suspend fun createAlert(alert: AlertLog): Long = alertLogDao.insertAlert(alert)

    suspend fun updateAlert(alert: AlertLog) = alertLogDao.updateAlert(alert)

    suspend fun endAlert(id: Long, isCancelled: Boolean = false, cancelMethod: String? = null) {
        alertLogDao.endAlert(id, System.currentTimeMillis(), isCancelled, cancelMethod)
    }

    suspend fun incrementSmsCount(id: Long) = alertLogDao.incrementSmsCount(id)

    suspend fun incrementCallCount(id: Long) = alertLogDao.incrementCallCount(id)

    suspend fun markEscalated(id: Long) = alertLogDao.markEscalated(id)

    suspend fun cleanupOldAlerts(olderThanMs: Long = 30L * 24 * 60 * 60 * 1000) {
        alertLogDao.deleteAlertsOlderThan(System.currentTimeMillis() - olderThanMs)
    }
}
