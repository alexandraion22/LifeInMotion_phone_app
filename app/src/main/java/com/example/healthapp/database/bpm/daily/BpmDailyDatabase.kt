package com.example.healthapp.database.bpm.daily

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BpmDaily::class], version = 1, exportSchema = false)
abstract class BpmDailyDatabase : RoomDatabase() {
    abstract fun bpmDailyDao(): BpmDailyDao

    companion object {
        @Volatile
        private var INSTANCE: BpmDailyDatabase? = null

        fun getDatabase(context: android.content.Context): BpmDailyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BpmDailyDatabase::class.java,
                    "bpm_daily_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}