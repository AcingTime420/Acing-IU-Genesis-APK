package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val correlationId: String = "",
    val category: String,
    val title: String,
    val details: String,
    val severity: String,
    val operatorRole: String,
    val deviceId: String = "",
    val outcome: String = "SUCCESS"
)

@Entity(tableName = "device_snapshots")
data class DeviceSnapshotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val deviceName: String,
    val androidVersion: String,
    val selinuxState: String,
    val avbState: String,
    val hardwareKeystore: String,
    val cveCount: Int,
    val healthScore: Int
)

@Entity(tableName = "firmware_scans")
data class FirmwareScanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val imageName: String,
    val partitionName: String,
    val sha256Hash: String,
    val signatureStatus: String,
    val isVerified: Boolean
)
