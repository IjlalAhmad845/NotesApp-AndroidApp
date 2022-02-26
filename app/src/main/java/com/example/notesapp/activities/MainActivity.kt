package com.example.notesapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.R
import com.example.notesapp.adapters.HomeRecyclerAdapter
import com.example.notesapp.dataModels.Notes
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.viewModels.HomeViewModel

class MainActivity : AppCompatActivity(), HomeRecyclerAdapter.CardOnClickInterface {

    private lateinit var binding: ActivityMainBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: HomeRecyclerAdapter
    private var actionMode: ActionMode? = null

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
        binding.homeToolbar.title = "Notes"
        binding.homeToolbar.setNavigationIcon(R.drawable.ic_home_menu)

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
            p0!!.menuInflater.inflate(R.menu.contextual_menu, p1)
            return true
        }

        override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
            return true
        }

        override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
            var deleteNoteIndex: Int
            when (p1!!.itemId) {
                R.id.contextual_delete -> {

                    //removing only selected items one by one
                    for (item in homeViewModel.selectedItems) {
                        deleteNoteIndex = homeViewModel.notesList.value!!.indexOf(item)
                        homeViewModel.deleteNote(deleteNoteIndex)
                        adapter.notifyItemRemoved(deleteNoteIndex)
                    }

                    //since all the selected items was removed above
                    homeViewModel.selectedItems.clear()
                }
            }

            //after contextual click, action mode should be finished
            actionMode!!.finish()

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

            actionMode = null
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
        if (actionMode == null)
            actionMode = startActionMode(ActionModeCallback())!!

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
            actionMode!!.finish()
        }

        adapter.notifyItemChanged(position)

        if (actionMode != null)
            actionMode!!.title = homeViewModel.selectedItems.size.toString()
    }
}