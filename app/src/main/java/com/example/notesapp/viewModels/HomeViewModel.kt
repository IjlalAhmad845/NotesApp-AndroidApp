package com.example.notesapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesapp.dataModels.Notes

class HomeViewModel : ViewModel() {
    private val _notesList=MutableLiveData<List<Notes>>()

    val notesList:LiveData<List<Notes>>
    get() = _notesList
}