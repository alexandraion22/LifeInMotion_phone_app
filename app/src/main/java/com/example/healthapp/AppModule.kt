package com.example.healthapp

import android.content.Context
import androidx.room.Room
import com.example.healthapp.database.bpm.BpmDao
import com.example.healthapp.database.bpm.BpmDatabase
import com.example.healthapp.database.bpm.BpmRepository
import com.example.healthapp.service.WatchListenerService
import dagger.Component
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
    fun provideDatabase(@ApplicationContext appContext: Context): BpmDatabase {
        return Room.databaseBuilder(
            appContext,
            BpmDatabase::class.java,
            "health_app_database"
        ).build()
    }
}