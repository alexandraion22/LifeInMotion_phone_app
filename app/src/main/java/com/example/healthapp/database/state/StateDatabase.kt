package com.example.healthapp.database.state

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [State::class], version = 1, exportSchema = false)
abstract class StateDatabase : RoomDatabase() {
    abstract fun stateDao(): StateDao

    companion object {
        @Volatile
        private var INSTANCE: StateDatabase? = null

        fun getDatabase(context: android.content.Context): StateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StateDatabase::class.java,
                    "state_current_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}