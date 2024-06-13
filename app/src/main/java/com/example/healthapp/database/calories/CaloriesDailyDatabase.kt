package com.example.healthapp.database.calories

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CaloriesDaily::class], version = 1, exportSchema = false)
abstract class CaloriesDailyDatabase : RoomDatabase() {
    abstract fun caloriesDailyDao(): CaloriesDailyDao

    companion object {
        @Volatile
        private var INSTANCE: CaloriesDailyDatabase? = null

        fun getDatabase(context: android.content.Context): CaloriesDailyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CaloriesDailyDatabase::class.java,
                    "calories_daily_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}