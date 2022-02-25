package com.example.notesapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesapp.dataModels.Notes

class HomeViewModel : ViewModel() {
    private val _notesList = MutableLiveData<MutableList<Notes>>(mutableListOf())

    init {
        _notesList.value!!.add(Notes("hello", "world", false))
    }

    val notesList: LiveData<MutableList<Notes>>
        get() = _notesList
}