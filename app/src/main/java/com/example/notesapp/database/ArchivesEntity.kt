package com.example.notesapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "archives_table")
data class ArchivesEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val body: String
)
