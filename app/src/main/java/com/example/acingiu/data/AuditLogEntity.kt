package com.example.acingiu.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.security.MessageDigest

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventContext: String,
    val securityType: String,
    val timestamp: Long = System.currentTimeMillis(),
    val validationDigest: String = ""
) {
    companion object {
        fun computeDigest(context: String, type: String, time: Long): String {
            val raw = "$context:$type:$time"
            val md = MessageDigest.getInstance("SHA-256")
            val digestBytes = md.digest(raw.toByteArray(Charsets.UTF_8))
            return digestBytes.joinToString("") { "%02x".format(it) }
        }
    }
}
