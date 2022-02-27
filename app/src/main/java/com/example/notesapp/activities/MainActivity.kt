package com.example.notesapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.R
import com.example.notesapp.adapters.HomeRecyclerAdapter
import com.example.notesapp.controllers.ActionModeController
import com.example.notesapp.controllers.NavigationMenuController
import com.example.notesapp.dataModels.Notes
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.viewModels.HomeViewModel


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
        binding.homeNavigationView.setNavigationItemSelectedListener(NavigationMenuController.mOnNavigationItemSelectedListener)

        mToggle.syncState()
        binding.homeToolbar.title = "Notes"

        adapter = HomeRecyclerAdapter(homeViewModel.displayNotesList, this)
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
                    adapter.notifyItemInserted(homeViewModel.displayNotesList.size)
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

            //staring add/edit note activity
            editNoteCallback.launch(intent)
        }
    }

    /**===================================================== CARD LONG CLICK =============================================================**/
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

    /**================================== METHOD SELECTION CONTROL ON CONTEXTUAL MENU ===========================================**/
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
}