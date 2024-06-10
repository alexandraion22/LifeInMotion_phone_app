package com.example.healthapp.database.steps.hourly

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StepsHourly::class], version = 1, exportSchema = false)
abstract class StepsHourlyDatabase : RoomDatabase() {
    abstract fun stepsHourlyDao(): StepsHourlyDao

    companion object {
        @Volatile
        private var INSTANCE: StepsHourlyDatabase? = null

        fun getDatabase(context: android.content.Context): StepsHourlyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StepsHourlyDatabase::class.java,
                    "steps_hourly_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}