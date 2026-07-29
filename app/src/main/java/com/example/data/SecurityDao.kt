package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    @Query("DELETE FROM audit_logs")
    suspend fun clearAuditLogs()

    @Query("SELECT * FROM device_snapshots ORDER BY timestamp DESC LIMIT 10")
    fun getRecentDeviceSnapshots(): Flow<List<DeviceSnapshotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceSnapshot(snapshot: DeviceSnapshotEntity)

    @Query("SELECT * FROM firmware_scans ORDER BY timestamp DESC")
    fun getAllFirmwareScans(): Flow<List<FirmwareScanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFirmwareScan(scan: FirmwareScanEntity)
}
