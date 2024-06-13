package com.example.healthapp.database.schedule

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [WorkoutSchedule::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WorkoutScheduleDatabase : RoomDatabase() {
    abstract fun workoutScheduleDao() : WorkoutScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: WorkoutScheduleDatabase? = null

        fun getDatabase(context: android.content.Context): WorkoutScheduleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutScheduleDatabase::class.java,
                    "workout_schedule_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}