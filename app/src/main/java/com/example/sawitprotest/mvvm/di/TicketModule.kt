package com.example.sawitprotest.mvvm.di

import android.content.Context
import androidx.room.Room
import com.example.sawitprotest.mvvm.database.TaskDatabase
import com.example.sawitprotest.mvvm.repository.TicketRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TicketModule {

    @Singleton
    @Provides
    fun provideTaskDatabase(@ApplicationContext context: Context): TaskDatabase {
        return Room.databaseBuilder(
            context,
            TaskDatabase::class.java,
            "ticket_database.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideTicketRepository(taskDatabase: TaskDatabase): TicketRepository {
        return TicketRepository(taskDatabase)
    }

}