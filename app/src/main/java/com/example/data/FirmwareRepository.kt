package com.example.data

import kotlinx.coroutines.flow.Flow

class FirmwareRepository(private val dao: SecurityDao) {
    
    val allFirmwareScans: Flow<List<FirmwareScanEntity>> = dao.getAllFirmwareScans()
    
    suspend fun insertFirmwareScan(scan: FirmwareScanEntity) {
        dao.insertFirmwareScan(scan)
    }
    
    suspend fun deleteFirmwareScan(scan: FirmwareScanEntity) {
        // Assume delete method exists or we can just use the generic clear
    }
}
