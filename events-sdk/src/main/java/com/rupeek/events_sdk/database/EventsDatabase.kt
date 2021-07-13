package com.rupeek.events_sdk.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rupeek.events_sdk.models.Events
import com.rupeek.events_sdk.models.TempTable

@Database(entities = arrayOf(Events::class, TempTable::class), version = 1, exportSchema = false)
abstract class EventsDatabase : RoomDatabase() {
    abstract fun eventsDao(): EventsDao

    companion object {
        @Volatile
        var INSTANCE: EventsDatabase? = null

        fun getInstance(context: Application): EventsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    EventsDatabase::class.java,
                    "EventsDatabase"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}