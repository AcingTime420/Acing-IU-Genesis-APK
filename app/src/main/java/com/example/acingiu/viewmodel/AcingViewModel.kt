package com.example.acingiu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acingiu.data.AuditLogDao
import com.example.acingiu.data.AuditLogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class AcingViewModel(
    private val auditLogDao: AuditLogDao
) : ViewModel() {

    val auditLogsState: StateFlow<List<AuditLogEntity>> = auditLogDao.getAllLogsSortedByTimestamp()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun logSecurityEvent(context: String, type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val timestamp = System.currentTimeMillis()
            val digest = AuditLogEntity.computeDigest(context, type, timestamp)
            val log = AuditLogEntity(
                eventContext = context,
                securityType = type,
                timestamp = timestamp,
                validationDigest = digest
            )
            auditLogDao.insertLog(log)
        }
    }

    fun clearLogs() {
        viewModelScope.launch(Dispatchers.IO) {
            auditLogDao.clearAllAuditLogs()
        }
    }
}
