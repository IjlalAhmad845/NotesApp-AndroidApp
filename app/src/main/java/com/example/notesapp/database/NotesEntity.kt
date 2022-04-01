package com.example.notesapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
data class NotesEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val body: String
)