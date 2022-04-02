package com.example.notesapp.dataModels

data class Notes(
    val head: String,
    val body: String,
    var isSelected: Boolean,
    var color: Int
)