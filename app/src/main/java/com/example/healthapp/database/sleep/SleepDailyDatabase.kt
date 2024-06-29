package com.example.healthapp.database.sleep

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SleepDaily::class], version = 1, exportSchema = false)
abstract class SleepDailyDatabase : RoomDatabase() {
    abstract fun sleepDailyDao(): SleepDailyDao

    companion object {
        @Volatile
        private var INSTANCE: SleepDailyDatabase? = null

        fun getDatabase(context: android.content.Context):SleepDailyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SleepDailyDatabase::class.java,
                    "sleep_daily_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}