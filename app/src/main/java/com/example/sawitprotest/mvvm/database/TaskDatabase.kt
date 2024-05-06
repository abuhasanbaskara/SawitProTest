package com.example.sawitprotest.mvvm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sawitprotest.mvvm.dao.TicketDao
import com.example.sawitprotest.mvvm.data.Ticket

@Database(entities = [Ticket::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun ticketDao(): TicketDao

    companion object {
        private const val DB_NAME = "ticket_database.db"
        @Volatile private var instance: TaskDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            TaskDatabase::class.java,
            DB_NAME
        ).build()
    }
}