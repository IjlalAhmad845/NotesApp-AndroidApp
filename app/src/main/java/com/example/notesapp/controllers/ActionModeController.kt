package com.example.notesapp.controllers

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import com.example.notesapp.R
import com.example.notesapp.adapters.HomeRecyclerAdapter
import com.example.notesapp.dataModels.Notes
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.viewModels.HomeViewModel
import com.google.android.material.snackbar.Snackbar

class ActionModeController(
    private val homeViewModel: HomeViewModel,
    private val adapter: HomeRecyclerAdapter,
    private val binding: ActivityMainBinding
) {
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
            actionItemClicked(p1)
            return true
        }

        override fun onDestroyActionMode(p0: ActionMode?) {
            actionModeDestroyed()
        }
    }

    /**==================================== METHOD FOR HANDLING ACTION ITEMS CLICKS =======================================**/
    private fun actionItemClicked(p1: MenuItem?) {
        var deleteNoteIndex: Int
        val deletedNoteIndexList: MutableList<Int> = mutableListOf()

        when (p1!!.itemId) {
            R.id.contextual_delete -> {

                //filling index list first, cause notes list size will vary when deleting notes
                for (item in homeViewModel.selectedItems) {
                    deleteNoteIndex = homeViewModel.displayNotesList.indexOf(item)
                    deletedNoteIndexList.add(deleteNoteIndex)
                }

                //removing only selected items one by one
                for (item in homeViewModel.selectedItems) {
                    deleteNoteIndex = homeViewModel.displayNotesList.indexOf(item)
                    homeViewModel.deleteNote(deleteNoteIndex)
                    adapter.notifyItemRemoved(deleteNoteIndex)
                }

                deleteSnackBar(deletedNoteIndexList, homeViewModel.selectedItems)
                homeViewModel.selectedItems.clear()
            }
        }

        homeViewModel.actionMode!!.finish()
    }

    /**==================================== METHOD FOR HANDLING ACTION MODE DESTROYED ====================================**/
    private fun actionModeDestroyed() {
        for (item in homeViewModel.selectedItems) {
            item.isSelected = false

            //notifying adapter for only those items which have changed
            //by finding their index
            adapter.notifyItemChanged(homeViewModel.displayNotesList.indexOf(item))
        }

        //resetting selection list
        homeViewModel.selectedItems.clear()
        homeViewModel.selectionMode = false

        homeViewModel.actionMode = null
    }

    /**========================================= METHOD FOR HANDLING DELETING SNACK BAR =======================================**/
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

