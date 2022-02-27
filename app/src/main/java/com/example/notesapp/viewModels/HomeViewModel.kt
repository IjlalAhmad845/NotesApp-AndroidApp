package com.example.notesapp.viewModels

import android.view.ActionMode
import androidx.lifecycle.ViewModel
import com.example.notesapp.dataModels.Notes

class HomeViewModel : ViewModel() {
    companion object {
        const val NOTE_TITLE_KEY = "com.example.notesapp.activities.note_title_key"
        const val NOTE_BODY_KEY = "com.example.notesapp.activities.note_body_key"
        const val NOTE_INDEX_KEY = "com.example.notesapp.activities.note_index_key"
    }

    private val _notesList: MutableList<Notes> = mutableListOf()
    val notesList: MutableList<Notes>
        get() = _notesList


    var actionMode: ActionMode? = null
    var selectionMode = false
    var selectedItems: MutableList<Notes> = mutableListOf()


    /**====================================== FUNCTION FOR ADDING NOTES TO NOTES LIST ========================================**/
    fun addNote(note: Notes) {
        _notesList.add(note)
    }

    fun addNoteAt(index: Int, note: Notes) {
        _notesList.add(index, note)
    }

    /**====================================== FUNCTION FOR UPDATING NOTES TO NOTES LIST ======================================**/
    fun editNote(note: Notes, index: Int) {
        _notesList[index] = note
    }

    /**====================================== FUNCTION FOR DELETING NOTES FROM NOTES LIST ====================================**/
    fun deleteNote(index: Int) {
        _notesList.removeAt(index)
    }

    /**==================================== FUNCTION FOR TOGGLING  SELECTION IN NOTES LIST ====================================**/
    fun setSelected(index: Int, isSelected: Boolean) {
        _notesList[index].isSelected = isSelected
    }
}