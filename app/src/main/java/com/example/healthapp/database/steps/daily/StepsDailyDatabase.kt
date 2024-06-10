package com.example.healthapp.database.steps.daily

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StepsDaily::class], version = 1, exportSchema = false)
abstract class StepsDailyDatabase : RoomDatabase() {
    abstract fun stepsDailyDao(): StepsDailyDao

    companion object {
        @Volatile
        private var INSTANCE: StepsDailyDatabase? = null

        fun getDatabase(context: android.content.Context): StepsDailyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StepsDailyDatabase::class.java,
                    "steps_daily_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}