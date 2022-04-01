package com.example.notesapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NotesEntity::class], version = 1)
abstract class NotesDB : RoomDatabase() {

    abstract fun EntityDao(): EntityDao

    companion object {

        @Volatile
        private var INSTANCE: NotesDB? = null

        fun getDatabase(context: Context): NotesDB {
            if (INSTANCE == null) {
                synchronized(this)
                {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        NotesDB::class.java,
                        "NotesDatabase"
                    ).build()
                }
            }

            return INSTANCE!!
        }
    }
}