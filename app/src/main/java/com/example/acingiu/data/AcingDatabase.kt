package com.example.acingiu.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AuditLogEntity::class], version = 1, exportSchema = false)
abstract class AcingDatabase : RoomDatabase() {

    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: AcingDatabase? = null

        fun getInstance(context: Context): AcingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AcingDatabase::class.java,
                    "acing_iu_security_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
