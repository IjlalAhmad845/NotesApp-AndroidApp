package com.example.notesapp.database

import androidx.room.*

@Dao
interface EntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(notesEntity: NotesEntity)

    @Query("Select * from notes_table")
    suspend fun getNotes(): MutableList<NotesEntity>

    @Query("Delete from notes_table")
    suspend fun deleteAllNotes()
}