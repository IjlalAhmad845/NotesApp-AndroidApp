package com.example.notesapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.notesapp.R
import com.example.notesapp.adapters.HomeRecyclerAdapter
import com.example.notesapp.controllers.ActionModeController
import com.example.notesapp.controllers.NavigationMenuController
import com.example.notesapp.dataModels.Notes
import com.example.notesapp.database.ArchivesEntity
import com.example.notesapp.database.NotesDB
import com.example.notesapp.database.NotesEntity
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.viewModels.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), HomeRecyclerAdapter.CardOnClickInterface {

    private lateinit var binding: ActivityMainBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: HomeRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        init()

        binding.homeFab.setOnClickListener {
            startAddNoteActivity()
        }
    }

    /**================================== METHOD FOR INITIALIZING TOOLBAR AND RECYCLER VIEW ==================================**/
    private fun init() {
        //INITIALIZING RECYCLER VIEW
        adapter = HomeRecyclerAdapter(homeViewModel.displayNotesList, this)
        binding.homeRv.adapter = adapter

        //PLACEHOLDER VIEW VISIBILITY CONTROL AFTER FETCHING NOTES COUNT FROM DATABASE
        //BECAUSE IT TAKES TIME
        lifecycleScope.launch(Dispatchers.IO) {
            val notesCount = NotesDB.getDatabase(this@MainActivity).EntityDao().getNotes().size
            //placeholder view
            binding.homePlaceholder.visibility = if (notesCount == 0) View.VISIBLE else View.GONE
        }

        //setting text in placeholder view on start
        binding.homePlaceholderTextView.text =
            this.getString(R.string.home_placeholder_text, "Notes")


        //INITIALIZING NAVIGATION MENU
        NavigationMenuController.initNavigationMenu(this, binding, homeViewModel, adapter)


        //REACTIVATING ACTION MODE IF IT WAS PREVIOUSLY TRIGGERED
        if (homeViewModel.actionMode != null) {
            homeViewModel.actionMode = startActionMode(
                ActionModeController(
                    homeViewModel,
                    adapter,
                    binding
                ).ActionModeCallback()
            )!!
            homeViewModel.actionMode!!.title = homeViewModel.selectedItems.size.toString()
        }
    }


    /**========================================== CALLBACK FOR RECEIVING NEW NOTE =================================================**/
    private var addNoteCallback =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it?.resultCode == Activity.RESULT_OK) {
                var noteTitle = it.data?.getStringExtra(AddNoteActivity.SEND_BACK_TITLE_KEY)
                var noteBody = it.data?.getStringExtra(AddNoteActivity.SEND_BACK_BODY_KEY)
                val noteOperation =
                    it.data?.getIntExtra(AddNoteActivity.SEND_BACK_NOTE_OPERATION_KEY, -1)

                noteTitle = noteTitle!!.trim()
                noteBody = noteBody!!.trim()

                if (!noteTitle.isNullOrEmpty() || !noteBody.isNullOrEmpty()) {
                    when (noteOperation) {
                        0 -> addNewNote(noteTitle, noteBody)
                        1 -> archiveNewNote(noteTitle, noteBody)
                    }

                    binding.homePlaceholder.visibility = View.GONE
                }
            }
        }

    /**================================================ CALLBACK FOR RECEIVING EDITED NOTE  =======================================**/
    private var editNoteCallback =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it?.resultCode == Activity.RESULT_OK) {
                var noteTitle = it.data?.getStringExtra(AddNoteActivity.SEND_BACK_TITLE_KEY)
                var noteBody = it.data?.getStringExtra(AddNoteActivity.SEND_BACK_BODY_KEY)
                val noteIndex = it.data?.getIntExtra(AddNoteActivity.SEND_BACK_INDEX_KEY, -1)!!
                val noteOperation =
                    it.data?.getIntExtra(AddNoteActivity.SEND_BACK_NOTE_OPERATION_KEY, -1)

                noteTitle = noteTitle!!.trim()
                noteBody = noteBody!!.trim()

                if (!noteTitle.isNullOrEmpty() || !noteBody.isNullOrEmpty()) {
                    when (noteOperation) {
                        //simply edit that note
                        0 -> editNote(noteTitle, noteBody, noteIndex)

                        //archive that note
                        1 -> archiveAndUnArchiveNote(noteIndex, noteTitle, noteBody)

                        //unarchive that note
                        2 -> archiveAndUnArchiveNote(noteIndex, noteTitle, noteBody)
                    }
                }
            }
        }

    /**======================================= METHOD FOR STARTING ADD NOTE ACTIVITY ===========================================**/
    private fun startAddNoteActivity() {
        val intent = Intent(this, AddNoteActivity::class.java)
            .putExtra(HomeViewModel.NOTE_TYPE_KEY, 0)
        addNoteCallback.launch(intent)
    }


    /**===================================================== CARD ON CLICK =============================================================**/
    override fun cardOnClick(position: Int) {

        //card selection mode
        if (homeViewModel.selectionMode) {
            notesSelectionControl(position)
        }
        //open note
        else {
            val intent = Intent(this, AddNoteActivity::class.java)
                .putExtra(
                    HomeViewModel.NOTE_TITLE_KEY,
                    homeViewModel.displayNotesList[position].head
                )
                .putExtra(
                    HomeViewModel.NOTE_BODY_KEY,
                    homeViewModel.displayNotesList[position].body
                )
                .putExtra(HomeViewModel.NOTE_INDEX_KEY, position)
                .putExtra(
                    HomeViewModel.NOTE_TYPE_KEY,
                    if (binding.homeToolbar.title == "Notes") 0 else 1
                )

            //staring add/edit note activity
            editNoteCallback.launch(intent)
        }
    }

    /**===================================================== CARD LONG CLICK ========================================================**/
    override fun cardLongClick(position: Int) {

        //CONTEXTUAL MENU STARTS BY SINGLETON PATTERN
        if (homeViewModel.actionMode == null)
            homeViewModel.actionMode = startActionMode(
                ActionModeController(
                    homeViewModel,
                    adapter,
                    binding
                ).ActionModeCallback()
            )!!

        homeViewModel.selectionMode = true
        notesSelectionControl(position)
    }

    /**================================== METHOD SELECTION CONTROL ON CONTEXTUAL MENU ====================================**/
    private fun notesSelectionControl(position: Int) {
        if (homeViewModel.selectedItems.contains(homeViewModel.displayNotesList[position])) {
            homeViewModel.setSelected(position, false)
            homeViewModel.selectedItems.remove(homeViewModel.displayNotesList[position])
        } else {
            homeViewModel.setSelected(position, true)
            homeViewModel.selectedItems.add(homeViewModel.displayNotesList[position])
        }

        if (homeViewModel.selectedItems.size == 0) {
            homeViewModel.selectionMode = false
            homeViewModel.actionMode!!.finish()
        }

        adapter.notifyItemChanged(position)

        if (homeViewModel.actionMode != null)
            homeViewModel.actionMode!!.title = homeViewModel.selectedItems.size.toString()
    }

    /**=================================== METHOD FOR ADDING NEW NOTE TO DISPLAY LIST =====================================**/
    private fun addNewNote(noteTitle: String, noteBody: String) {
        homeViewModel.addDisplayNote(Notes(noteTitle, noteBody, false))
        adapter.notifyItemInserted(homeViewModel.displayNotesList.size)
    }

    /**========================================= METHOD FOR EDIT  NOTE OF DISPLAY LIST ========================================**/
    private fun editNote(noteTitle: String, noteBody: String, noteIndex: Int) {
        homeViewModel.editDisplayNote(
            Notes(noteTitle, noteBody, false),
            noteIndex
        )
        adapter.notifyItemChanged(noteIndex)
    }

    /**================================= METHOD FOR ARCHIVE NEW NOTE OF DISPLAY LIST =======================================**/
    private fun archiveNewNote(noteTitle: String, noteBody: String) {
        archiveSnackBar(noteTitle, noteBody)
    }

    /**================================= METHOD FOR ARCHIVE EXISTING NOTE OF DISPLAY LIST =================================**/
    private fun archiveAndUnArchiveNote(
        noteIndex: Int,
        noteTitle: String,
        noteBody: String
    ) {
        homeViewModel.deleteDisplayNote(noteIndex)
        adapter.notifyItemRemoved(noteIndex)

        archiveSnackBar(noteTitle, noteBody)
    }

    /**===================================== METHOD FOR FOR HANDLING ARCHIVE SNACK BAR =====================================**/
    private fun archiveSnackBar(noteTitle: String, noteBody: String) {
        val note = Notes(noteTitle, noteBody, false)
        val isNoteSection = binding.homeToolbar.title == "Notes"

        //ARCHIVING OR UnARCHIVING Based on Section
        if (isNoteSection)
            homeViewModel.addToArchive(note)
        else
            homeViewModel.addToNotes(note)

        val snackBar = Snackbar.make(
            binding.homeRootLayout,
            if (isNoteSection) "Note Archived" else "Note UnArchived",
            Snackbar.LENGTH_SHORT
        )
            .setAction("UNDO") {
                homeViewModel.addDisplayNote(note)
                adapter.notifyItemInserted(homeViewModel.displayNotesList.size)

                //removing notes from opposite end
                if (isNoteSection)
                    homeViewModel.deleteFromArchived(note)
                else
                    homeViewModel.deleteFromNotes(note)

                Snackbar.make(
                    binding.homeRootLayout,
                    if (isNoteSection) "Note Recovered" else "Archive Recovered",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        snackBar.show()

        //KILLING SNACK BAR WHEN NAVIGATION DRAWER IS OPENED FOR REMOVING UNDO FUNCTIONALITY
        binding.homeDrawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {
                snackBar.dismiss()
            }

            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })
    }

    /**=================================== METHOD FOR SAVING CHANGES TO DATABASE ON EXIT ============================**/
    private fun saveChanges() {

        lifecycleScope.launch {
            val dao = NotesDB.getDatabase(this@MainActivity).EntityDao()
            dao.deleteAllNotes()
            dao.deleteAllArchives()

            var itr = 0
            if (binding.homeToolbar.title == "Notes") {
                for (note in homeViewModel.displayNotesList) {
                    dao.insertNote(NotesEntity(itr++, note.head, note.body))
                }
                itr = 0
                for (note in homeViewModel.archivesList) {
                    dao.insertArchive(ArchivesEntity(itr++, note.head, note.body))
                }
            } else {
                for (note in homeViewModel.displayNotesList) {
                    dao.insertArchive(ArchivesEntity(itr++, note.head, note.body))
                }
                itr = 0
                for (note in homeViewModel.notesList) {
                    dao.insertNote(NotesEntity(itr++, note.head, note.body))
                }
            }
        }
    }

    override fun onStop() {
        saveChanges()
        super.onStop()
    }
}