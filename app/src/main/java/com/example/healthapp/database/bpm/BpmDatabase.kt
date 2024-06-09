package com.example.healthapp.database.bpm

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Bpm::class], version = 1, exportSchema = false)
@TypeConverters(LocalDateTimeConverter::class)
abstract class BpmDatabase : RoomDatabase() {
    abstract fun bpmDao(): BpmDao

    companion object {
        @Volatile
        private var INSTANCE: BpmDatabase? = null

        fun getDatabase(context: android.content.Context): BpmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BpmDatabase::class.java,
                    "bpm_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}