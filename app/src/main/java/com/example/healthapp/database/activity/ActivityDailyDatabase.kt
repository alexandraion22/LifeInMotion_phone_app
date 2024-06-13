package com.example.healthapp.database.activity

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ActivityDaily::class], version = 1, exportSchema = false)
abstract class ActivityDailyDatabase : RoomDatabase() {
    abstract fun activityDailyDao(): ActivityDailyDao

    companion object {
        @Volatile
        private var INSTANCE: ActivityDailyDatabase? = null

        fun getDatabase(context: android.content.Context): ActivityDailyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ActivityDailyDatabase::class.java,
                    "activity_daily_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}