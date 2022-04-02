package com.example.notesapp.viewModels

import android.app.Application
import android.view.ActionMode
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.dataModels.Notes
import com.example.notesapp.database.NotesDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val NOTE_TITLE_KEY = "com.example.notesapp.activities.note_title_key"
        const val NOTE_BODY_KEY = "com.example.notesapp.activities.note_body_key"
        const val NOTE_INDEX_KEY = "com.example.notesapp.activities.note_index_key"
        const val NOTE_TYPE_KEY = "com.example.notesapp.activities.note_type_key"
        const val NOTE_COLOR_KEY = "com.example.notesapp.activities.note_color_key"
    }

    private var _displayNotesList: MutableList<Notes> = mutableListOf()
    val displayNotesList: MutableList<Notes>
        get() = _displayNotesList

    private var _notesList: MutableList<Notes> = mutableListOf()
    val notesList: MutableList<Notes>
        get() = _notesList
    private var _archivesList: MutableList<Notes> = mutableListOf()
    val archivesList: MutableList<Notes>
        get() = _archivesList


    var actionMode: ActionMode? = null
    var selectionMode = false
    var selectedItems: MutableList<Notes> = mutableListOf()
    var isNotesSection = true

    //FIRST CALL OF VIEW MODEL
    init {
        val dao = NotesDB.getDatabase(application).EntityDao()

        viewModelScope.launch(Dispatchers.IO) {
            //getting notes and archives
            val notesList = dao.getNotes()
            val archivesList = dao.getArchives()

            //adding to notes list
            for (note in notesList) {
                _notesList.add(Notes(note.title, note.body, false, note.color))
            }
            //adding to archives list
            for (note in archivesList) {
                _archivesList.add(Notes(note.title, note.body, false, note.color))
            }

            //setting notes to display list
            _displayNotesList.clear()
            _displayNotesList.addAll(_notesList)
        }
    }

    /**=================================== FUNCTION FOR ADDING NOTES TO DISPLAYED LIST =======================================**/
    fun addDisplayNote(note: Notes) {
        _displayNotesList.add(note)
    }

    /**============================ FUNCTION FOR ADDING NOTES TO DISPLAYED LIST AT INDEX ===================================**/
    fun addDisplayNoteAt(index: Int, note: Notes) {
        _displayNotesList.add(index, note)
    }

    /**===================================== FUNCTION FOR UPDATING NOTES TO DISPLAYED LIST ==================================**/
    fun editDisplayNote(note: Notes, index: Int) {
        _displayNotesList[index] = note
    }

    /**====================================== FUNCTION FOR DELETING NOTES FROM DISPLAYED LIST ===============================**/
    fun deleteDisplayNote(index: Int) {
        _displayNotesList.removeAt(index)
    }

    /**=================================== FUNCTION FOR ADDING ARCHIVES TO ARCHIVES LIST ===================================**/
    fun addToArchive(note: Notes) {
        _archivesList.add(note)
    }

    /**=================================== FUNCTION FOR REMOVING ARCHIVES FROM ARCHIVES LIST =============================**/
    fun deleteFromArchived(note: Notes) {
        _archivesList.remove(note)
    }

    /**====================================== FUNCTION FOR ADDING NOTES TO NOTES LIST  ======================================**/
    fun addToNotes(note: Notes) {
        _notesList.add(note)
    }

    /**===================================== FUNCTION FOR REMOVING NOTES FROM NOTES LIST  ==================================**/
    fun deleteFromNotes(note: Notes) {
        _notesList.remove(note)
    }

    /**==================================== FUNCTION FOR TOGGLING  SELECTION IN NOTES LIST ================================**/
    fun setSelected(index: Int, isSelected: Boolean) {
        _displayNotesList[index].isSelected = isSelected
    }

    /**========================================== FUNCTION FOR SETTING COLOR TO NOTES =======================================**/
    fun setColor(index: Int, color: Int) {
        _displayNotesList[index].color = color
    }

    /**===================================== FUNCTION FOR SWITCHING  NOTES TO ARCHIVES ======================================**/
    fun switchToNotes() {

        _archivesList.clear()
        _archivesList.addAll(_displayNotesList)

        _displayNotesList.clear()
        _displayNotesList.addAll(_notesList)
    }

    /**====================================== FUNCTION FOR SWITCHING  ARCHIVES TO NOTES =====================================**/
    fun switchToArchives() {
        _notesList.clear()
        _notesList.addAll(_displayNotesList)

        _displayNotesList.clear()
        _displayNotesList.addAll(_archivesList)
    }
}