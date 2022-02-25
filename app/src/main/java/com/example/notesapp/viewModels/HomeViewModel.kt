package com.example.notesapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesapp.adapters.HomeRecyclerAdapter
import com.example.notesapp.dataModels.Notes

class HomeViewModel : ViewModel() {
    companion object {
        const val NOTE_TITLE_KEY = "com.example.notesapp.activities.note_title_key"
        const val NOTE_BODY_KEY = "com.example.notesapp.activities.note_body_key"
        const val NOTE_INDEX_KEY = "com.example.notesapp.activities.note_index_key"
    }

    private val _notesList = MutableLiveData<MutableList<Notes>>(mutableListOf())

    val notesList: LiveData<MutableList<Notes>>
        get() = _notesList

    /**====================================== FUNCTION FOR ADDING NOTES TO NOTES LIST ========================================**/
    fun addNote(note: Notes) {
        _notesList.value?.add(note)
    }

    /**====================================== FUNCTION FOR UPDATING NOTES TO NOTES LIST ======================================**/
    fun editNote(note: Notes, index: Int) {
        _notesList.value?.set(index, note)
    }
}