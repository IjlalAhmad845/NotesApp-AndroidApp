package com.example.notesapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesapp.adapters.HomeRecyclerAdapter
import com.example.notesapp.dataModels.Notes

class HomeViewModel : ViewModel() {
    private val _notesList = MutableLiveData<MutableList<Notes>>(mutableListOf())
    var adapter: HomeRecyclerAdapter = HomeRecyclerAdapter(_notesList.value!!)

    val notesList: LiveData<MutableList<Notes>>
        get() = _notesList

    fun addNote(note: Notes) {
        _notesList.value?.add(note)
    }
}