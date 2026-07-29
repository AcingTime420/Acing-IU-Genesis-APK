package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [AuditLogEntity::class, DeviceSnapshotEntity::class, FirmwareScanEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AcingDatabase : RoomDatabase() {
    abstract fun securityDao(): SecurityDao

    companion object {
        @Volatile
        private var INSTANCE: AcingDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE audit_logs ADD COLUMN correlationId TEXT NOT NULL DEFAULT ''")
                } catch (e: Exception) {
                    // Column already exists or table freshly created
                }
                try {
                    db.execSQL("ALTER TABLE audit_logs ADD COLUMN deviceId TEXT NOT NULL DEFAULT ''")
                } catch (e: Exception) {
                    // Column already exists
                }
                try {
                    db.execSQL("ALTER TABLE audit_logs ADD COLUMN outcome TEXT NOT NULL DEFAULT 'SUCCESS'")
                } catch (e: Exception) {
                    // Column already exists
                }
            }
        }

        fun getDatabase(context: Context): AcingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AcingDatabase::class.java,
                    "acing_genesis_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
