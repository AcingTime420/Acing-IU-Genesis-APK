package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import kotlinx.coroutines.flow.Flow

/**
 * Immutable audit trail entity tracking firmware analysis activities and system security events.
 */
@Entity(tableName = "security_audit_trail")
data class SecurityAuditEventEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "event_id")
    val event_id: Long = 0,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "security_level")
    val security_level: String,

    @ColumnInfo(name = "message")
    val message: String
)

@Dao
interface SecurityAuditDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditEvent(event: SecurityAuditEventEntity): Long

    @Query("SELECT * FROM security_audit_trail ORDER BY timestamp DESC")
    fun getAllAuditEvents(): Flow<List<SecurityAuditEventEntity>>

    @Query("SELECT * FROM security_audit_trail ORDER BY timestamp DESC")
    suspend fun getAllAuditEventsList(): List<SecurityAuditEventEntity>

    @Query("SELECT * FROM security_audit_trail ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentAuditEvents(limit: Int): List<SecurityAuditEventEntity>

    @Query("SELECT * FROM security_audit_trail WHERE security_level = :level ORDER BY timestamp DESC")
    fun getEventsByLevel(level: String): Flow<List<SecurityAuditEventEntity>>

    @Query("SELECT COUNT(*) FROM security_audit_trail")
    suspend fun getCount(): Int
}

/**
 * Dedicated Room database storing immutable security audit events for firmware analysis activities.
 */
@Database(
    entities = [SecurityAuditEventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SecurityAuditDatabase : RoomDatabase() {
    abstract fun securityAuditDao(): SecurityAuditDao

    companion object {
        @Volatile
        private var INSTANCE: SecurityAuditDatabase? = null

        fun getDatabase(context: Context): SecurityAuditDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SecurityAuditDatabase::class.java,
                    "security_audit_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
