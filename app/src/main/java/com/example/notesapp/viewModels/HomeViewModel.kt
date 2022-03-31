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

    private var _displayNotesList: MutableList<Notes> = mutableListOf()
    val displayNotesList: MutableList<Notes>
        get() = _displayNotesList

    private var _notesList: MutableList<Notes> = mutableListOf()
    private var _archivesList: MutableList<Notes> = mutableListOf()


    var actionMode: ActionMode? = null
    var selectionMode = false
    var selectedItems: MutableList<Notes> = mutableListOf()


    /**====================================== FUNCTION FOR ADDING NOTES TO NOTES LIST ========================================**/
    fun addDisplayNote(note: Notes) {
        _displayNotesList.add(note)
    }

    fun addDisplayNoteAt(index: Int, note: Notes) {
        _displayNotesList.add(index, note)
    }

    /**====================================== FUNCTION FOR UPDATING NOTES TO NOTES LIST ======================================**/
    fun editDisplayNote(note: Notes, index: Int) {
        _displayNotesList[index] = note
    }

    /**====================================== FUNCTION FOR DELETING NOTES FROM NOTES LIST ====================================**/
    fun deleteDisplayNote(index: Int) {
        _displayNotesList.removeAt(index)
    }

    fun addToArchive(note: Notes) {
        _archivesList.add(note);
    }

    fun deleteFromArchived(note: Notes) {
        _archivesList.remove(note);
    }

    fun addToNotes(note: Notes) {
        _archivesList.add(note);
    }

    /**==================================== FUNCTION FOR TOGGLING  SELECTION IN NOTES LIST ====================================**/
    fun setSelected(index: Int, isSelected: Boolean) {
        _displayNotesList[index].isSelected = isSelected
    }

    fun switchToNotes() {

        _archivesList.clear()
        _archivesList.addAll(_displayNotesList)

        _displayNotesList.clear()
        _displayNotesList.addAll(_notesList)
    }

    fun switchToArchives() {
        _notesList.clear()
        _notesList.addAll(_displayNotesList)

        _displayNotesList.clear()
        _displayNotesList.addAll(_archivesList)
    }
}