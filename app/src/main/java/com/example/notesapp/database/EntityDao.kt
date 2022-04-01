package com.example.notesapp.database

import androidx.room.*

@Dao
interface EntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(notesEntity: NotesEntity)

    @Query("Select * from notes_table")
    suspend fun getNotes(): MutableList<NotesEntity>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(notesEntity: NotesEntity)

    @Delete
    suspend fun deleteNote(notesEntity: NotesEntity)
}