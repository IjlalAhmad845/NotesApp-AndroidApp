package com.example.notesapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.R
import com.example.notesapp.adapters.HomeRecyclerAdapter
import com.example.notesapp.dataModels.Notes
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.viewModels.HomeViewModel
import com.google.android.material.snackbar.Snackbar


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

        //reactivating action mode for screen rotation change if it is previously triggered
        if (homeViewModel.actionMode != null) {
            homeViewModel.actionMode = startActionMode(ActionModeCallback())!!
            homeViewModel.actionMode!!.title = homeViewModel.selectedItems.size.toString()
        }

        //SETTING NAVIGATION MENU
        val mToggle =
            ActionBarDrawerToggle(
                this,
                binding.homeDrawer,
                binding.homeToolbar,
                R.string.open,
                R.string.close
            )
        binding.homeDrawer.addDrawerListener(mToggle)

        mToggle.syncState()
        binding.homeToolbar.title = "Notes"

        adapter = HomeRecyclerAdapter(homeViewModel.notesList.value!!, this)
        binding.homeRv.adapter = adapter
    }

    /**========================================== CALLBACK FOR RECEIVING NEW NOTE =================================================**/
    private var addNoteCallback =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it?.resultCode == Activity.RESULT_OK) {
                val noteTitle = it.data?.getStringExtra(AddNoteActivity.SEND_BACK_TITLE_KEY)
                val noteBody = it.data?.getStringExtra(AddNoteActivity.SEND_BACK_BODY_KEY)

                if (!noteTitle.isNullOrEmpty() || !noteBody.isNullOrEmpty()) {
                    homeViewModel.addNote(Notes(noteTitle!!.trim(), noteBody!!.trim(), false))
                    adapter.notifyItemInserted(homeViewModel.notesList.value!!.size)
                }
            }
        }

    /**================================================ CALLBACK FOR RECEIVING EDITED NOTE  =======================================**/
    private var editNoteCallback =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it?.resultCode == Activity.RESULT_OK) {
                val noteTitle = it.data?.getStringExtra(AddNoteActivity.SEND_BACK_TITLE_KEY)
                val noteBody = it.data?.getStringExtra(AddNoteActivity.SEND_BACK_BODY_KEY)
                val noteIndex = it.data?.getIntExtra(AddNoteActivity.SEND_BACK_INDEX_KEY, -1)!!

                if (!noteTitle.isNullOrEmpty() || !noteBody.isNullOrEmpty()) {
                    homeViewModel.editNote(
                        Notes(noteTitle!!.trim(), noteBody!!.trim(), false),
                        noteIndex
                    )
                    adapter.notifyItemChanged(noteIndex)
                }
            }
        }

    /**======================================= METHOD FOR STARTING ADD NOTE ACTIVITY =============================================**/
    private fun startAddNoteActivity() {
        val intent = Intent(this, AddNoteActivity::class.java)
        addNoteCallback.launch(intent)
    }

    /**=================================================== CALLBACK FOR CONTEXTUAL MENU =============================================**/
    inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
            p0!!.menuInflater.inflate(R.menu.menu_contextual, p1)
            return true
        }

        override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
            return true
        }

        override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
            var deleteNoteIndex: Int
            val deletedNoteIndexList: MutableList<Int> = mutableListOf()

            when (p1!!.itemId) {
                R.id.contextual_delete -> {

                    //filling index list first, cause notes list size will vary when deleting notes
                    for (item in homeViewModel.selectedItems) {
                        deleteNoteIndex = homeViewModel.notesList.value!!.indexOf(item)
                        deletedNoteIndexList.add(deleteNoteIndex)
                    }

                    //removing only selected items one by one
                    for (item in homeViewModel.selectedItems) {
                        deleteNoteIndex = homeViewModel.notesList.value!!.indexOf(item)
                        homeViewModel.deleteNote(deleteNoteIndex)
                        adapter.notifyItemRemoved(deleteNoteIndex)
                    }

                    deleteSnackBar(deletedNoteIndexList, homeViewModel.selectedItems)
                    homeViewModel.selectedItems.clear()
                }
            }

            homeViewModel.actionMode!!.finish()
            return true
        }

        override fun onDestroyActionMode(p0: ActionMode?) {

            for (item in homeViewModel.selectedItems) {
                item.isSelected = false

                //notifying adapter for only those items which have changed
                //by finding their index
                adapter.notifyItemChanged(homeViewModel.notesList.value!!.indexOf(item))
            }

            //resetting selection list
            homeViewModel.selectedItems.clear()
            homeViewModel.selectionMode = false

            homeViewModel.actionMode = null
        }
    }


    /**===================================================== CARD ON CLICK =============================================================**/
    override fun cardOnClick(position: Int) {

        //card selection mode
        if (homeViewModel.selectionMode) {
            contextualMenuControl(position)
        }
        //open note
        else {
            val intent = Intent(this, AddNoteActivity::class.java)

            intent.putExtra(
                HomeViewModel.NOTE_TITLE_KEY,
                homeViewModel.notesList.value?.get(position)?.head
            )

            intent.putExtra(
                HomeViewModel.NOTE_BODY_KEY,
                homeViewModel.notesList.value?.get(position)?.body
            )

            intent.putExtra(HomeViewModel.NOTE_INDEX_KEY, position)
            editNoteCallback.launch(intent)
        }
    }

    /**===================================================== CARD LONG CLICK =============================================================**/
    override fun cardLongClick(position: Int) {

        //contextual menu opens by singleton pattern
        if (homeViewModel.actionMode == null)
            homeViewModel.actionMode = startActionMode(ActionModeCallback())!!

        homeViewModel.selectionMode = true
        contextualMenuControl(position)
    }

    /**================================== METHOD SELECTION CONTROL ON CONTEXTUAL MENU ===========================================**/
    private fun contextualMenuControl(position: Int) {
        if (homeViewModel.selectedItems.contains(homeViewModel.notesList.value!![position])) {
            homeViewModel.setSelected(position, false)
            homeViewModel.selectedItems.remove(homeViewModel.notesList.value!![position])
        } else {
            homeViewModel.setSelected(position, true)
            homeViewModel.selectedItems.add(homeViewModel.notesList.value!![position])
        }

        if (homeViewModel.selectedItems.size == 0) {
            homeViewModel.selectionMode = false
            homeViewModel.actionMode!!.finish()
        }

        adapter.notifyItemChanged(position)

        if (homeViewModel.actionMode != null)
            homeViewModel.actionMode!!.title = homeViewModel.selectedItems.size.toString()
    }

    /**========================================= METHOD FOR HANDLING DELETING SNACK BAR ==============================================**/
    private fun deleteSnackBar(
        indexList: MutableList<Int>,
        NotesList: MutableList<Notes>
    ) {

        //mapping indexes to notes list
        var notesMapList = mutableMapOf<Int, Notes>()
        for (i in 0 until indexList.size)
            notesMapList[indexList[i]] = NotesList[i]

        //sorting indexes for removing selection order
        indexList.sort()
        notesMapList = notesMapList.toSortedMap()

        //making snack bar
        val snackBar = Snackbar.make(
            binding.homeRootLayout,
            if (notesMapList.size > 1) "Notes Deleted" else "Note Deleted",
            Snackbar.LENGTH_LONG
        )

        //DISMISS LISTENER
        snackBar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                indexList.clear()
                notesMapList.clear()
            }
        })

        //SNACK BAR UNDO BUTTON
        snackBar.setAction("UNDO") {
            println(notesMapList)

            for (i in indexList) {
                notesMapList[i]!!.isSelected = false
                homeViewModel.addNoteAt(i, notesMapList[i]!!)
                adapter.notifyItemInserted(i)
            }

            Snackbar.make(
                binding.homeRootLayout,
                if (notesMapList.size > 1) "Notes Recovered" else "Note Recovered",
                Snackbar.LENGTH_SHORT
            ).show()
        }
        snackBar.show()
    }
}