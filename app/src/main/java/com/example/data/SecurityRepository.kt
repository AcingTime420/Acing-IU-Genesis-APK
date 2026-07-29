package com.example.data

import kotlinx.coroutines.flow.Flow

class SecurityRepository(private val dao: SecurityDao) {

    val auditLogs: Flow<List<AuditLogEntity>> = dao.getAllAuditLogs()
    val deviceSnapshots: Flow<List<DeviceSnapshotEntity>> = dao.getRecentDeviceSnapshots()
    val firmwareScans: Flow<List<FirmwareScanEntity>> = dao.getAllFirmwareScans()

    suspend fun logEvent(
        category: String,
        title: String,
        details: String,
        severity: String = "INFO",
        role: String = "Principal Architect",
        correlationId: String = "",
        deviceId: String = "",
        outcome: String = "SUCCESS"
    ) {
        val entry = AuditLogEntity(
            correlationId = correlationId,
            category = category,
            title = title,
            details = details,
            severity = severity,
            operatorRole = role,
            deviceId = deviceId,
            outcome = outcome
        )
        dao.insertAuditLog(entry)
    }

    suspend fun recordDeviceSnapshot(snapshot: DeviceSnapshotEntity) {
        dao.insertDeviceSnapshot(snapshot)
    }

    suspend fun recordFirmwareScan(scan: FirmwareScanEntity) {
        dao.insertFirmwareScan(scan)
    }

    suspend fun clearLogs() {
        dao.clearAuditLogs()
    }
}
