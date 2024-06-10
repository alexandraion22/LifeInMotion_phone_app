package com.example.healthapp.database.bpm.hourly

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BpmHourly::class], version = 1, exportSchema = false)
abstract class BpmHourlyDatabase : RoomDatabase() {
    abstract fun bpmHourlyDao(): BpmHourlyDao

    companion object {
        @Volatile
        private var INSTANCE: BpmHourlyDatabase? = null

        fun getDatabase(context: android.content.Context): BpmHourlyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BpmHourlyDatabase::class.java,
                    "bpm_hourly_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}