package com.example.healthapp.database.goals

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Goals::class], version = 1, exportSchema = false)
abstract class GoalsDatabase : RoomDatabase() {
    abstract fun goalsDao(): GoalsDao

    companion object {
        @Volatile
        private var INSTANCE: GoalsDatabase? = null

        fun getDatabase(context: android.content.Context): GoalsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GoalsDatabase::class.java,
                    "goals_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}