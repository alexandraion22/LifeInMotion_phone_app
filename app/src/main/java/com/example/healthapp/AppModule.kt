package com.example.healthapp

import android.content.Context
import androidx.room.Room
import com.example.healthapp.database.bpm.daily.BpmDaily
import com.example.healthapp.database.bpm.daily.BpmDailyDao
import com.example.healthapp.database.bpm.daily.BpmDailyDatabase
import com.example.healthapp.database.bpm.daily.BpmDailyRepository
import com.example.healthapp.database.bpm.hourly.BpmHourly
import com.example.healthapp.database.bpm.hourly.BpmHourlyDao
import com.example.healthapp.database.bpm.hourly.BpmHourlyDatabase
import com.example.healthapp.database.bpm.hourly.BpmHourlyRepository
import com.example.healthapp.database.bpm.last.BpmDao
import com.example.healthapp.database.bpm.last.BpmDatabase
import com.example.healthapp.database.bpm.last.BpmRepository
import com.example.healthapp.database.schedule.WorkoutScheduleDao
import com.example.healthapp.database.schedule.WorkoutScheduleDatabase
import com.example.healthapp.database.schedule.WorkoutScheduleRepository
import com.example.healthapp.database.steps.daily.StepsDailyDao
import com.example.healthapp.database.steps.daily.StepsDailyDatabase
import com.example.healthapp.database.steps.daily.StepsDailyRepository
import com.example.healthapp.database.steps.hourly.StepsHourlyDao
import com.example.healthapp.database.steps.hourly.StepsHourlyDatabase
import com.example.healthapp.database.steps.hourly.StepsHourlyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBpmDao(database: BpmDatabase): BpmDao {
        return database.bpmDao()
    }

    @Provides
    @Singleton
    fun provideBpmRepository(bpmDao: BpmDao): BpmRepository {
        return BpmRepository(bpmDao)
    }

    @Provides
    @Singleton
    fun provideBpmDatabase(@ApplicationContext appContext: Context): BpmDatabase {
        return Room.databaseBuilder(
            appContext,
            BpmDatabase::class.java,
            "health_app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideStepsHourlyDao(database: StepsHourlyDatabase): StepsHourlyDao {
        return database.stepsHourlyDao()
    }

    @Provides
    @Singleton
    fun provideStepsHourlyRepository(stepsHourlyDao: StepsHourlyDao): StepsHourlyRepository {
        return StepsHourlyRepository(stepsHourlyDao)
    }

    @Provides
    @Singleton
    fun provideStepsHourlyDatabase(@ApplicationContext appContext: Context): StepsHourlyDatabase {
        return Room.databaseBuilder(
            appContext,
            StepsHourlyDatabase::class.java,
            "steps_hourly_database"
        ).build()
    }


    @Provides
    @Singleton
    fun provideStepsDailyDao(database: StepsDailyDatabase): StepsDailyDao {
        return database.stepsDailyDao()
    }

    @Provides
    @Singleton
    fun provideStepsDailyRepository(stepsDailyDao: StepsDailyDao): StepsDailyRepository {
        return StepsDailyRepository(stepsDailyDao)
    }

    @Provides
    @Singleton
    fun provideStepsDailyDatabase(@ApplicationContext appContext: Context): StepsDailyDatabase {
        return Room.databaseBuilder(
            appContext,
            StepsDailyDatabase::class.java,
            "steps_daily_database"
        ).build()
    }


    @Provides
    @Singleton
    fun provideBpmHourlyDao(database: BpmHourlyDatabase): BpmHourlyDao {
        return database.bpmHourlyDao()
    }

    @Provides
    @Singleton
    fun provideBpmHourlyRepository(bpmHourlyDao: BpmHourlyDao): BpmHourlyRepository {
        return BpmHourlyRepository(bpmHourlyDao)
    }

    @Provides
    @Singleton
    fun provideBpmHourlyDatabase(@ApplicationContext appContext: Context): BpmHourlyDatabase {
        return Room.databaseBuilder(
            appContext,
            BpmHourlyDatabase::class.java,
            "bpm_hourly_database"
        ).build()
    }


    @Provides
    @Singleton
    fun provideBpmDailyDao(database: BpmDailyDatabase): BpmDailyDao {
        return database.bpmDailyDao()
    }

    @Provides
    @Singleton
    fun provideBpmDailyRepository(bpmDailyDao: BpmDailyDao): BpmDailyRepository {
        return BpmDailyRepository(bpmDailyDao)
    }

    @Provides
    @Singleton
    fun provideBpmDailyDatabase(@ApplicationContext appContext: Context): BpmDailyDatabase {
        return Room.databaseBuilder(
            appContext,
            BpmDailyDatabase::class.java,
            "bpm_daily_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWorkoutScheduleDao(database: WorkoutScheduleDatabase): WorkoutScheduleDao {
        return database.workoutScheduleDao()
    }

    @Provides
    @Singleton
    fun provideWorkoutScheduleRepository(workoutScheduleDao: WorkoutScheduleDao): WorkoutScheduleRepository {
        return WorkoutScheduleRepository(workoutScheduleDao)
    }

    @Provides
    @Singleton
    fun provideWorkoutScheduleDatabase(@ApplicationContext appContext: Context): WorkoutScheduleDatabase {
        return Room.databaseBuilder(
            appContext,
            WorkoutScheduleDatabase::class.java,
            "workout_schedule_database"
        ).build()
    }

}