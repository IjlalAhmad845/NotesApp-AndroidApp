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


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArchive(archivesEntity: ArchivesEntity)

    @Query("Select * from archives_table")
    suspend fun getArchives(): MutableList<ArchivesEntity>

    @Query("Delete from archives_table")
    suspend fun deleteAllArchives()
}