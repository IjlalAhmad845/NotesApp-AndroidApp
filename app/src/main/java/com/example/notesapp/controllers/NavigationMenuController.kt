package com.example.notesapp.controllers

import android.app.Activity
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.notesapp.R
import com.example.notesapp.adapters.HomeRecyclerAdapter
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.viewModels.HomeViewModel

class NavigationMenuController {
    companion object {

        /**====================================== FUNCTION FOR INITIALIZING NAVIGATION MENU ======================================**/
        fun initNavigationMenu(
            context: Activity,
            binding: ActivityMainBinding,
            homeViewModel: HomeViewModel,
            adapter: HomeRecyclerAdapter
        ) {
            //SETTING NAVIGATION MENU
            val mToggle =
                ActionBarDrawerToggle(
                    context,
                    binding.homeDrawer,
                    binding.homeToolbar,
                    R.string.open,
                    R.string.close
                )
            binding.homeDrawer.addDrawerListener(mToggle)
            mToggle.syncState()

            //setting toolbar title (considering screen rotation)
            binding.homeToolbar.title = if (homeViewModel.isNotesSection) "Notes" else "Archives"

            navItemClickListener(binding, homeViewModel, adapter)
        }

        /**======================================= FUNCTION FOR HANDLING NAV ITEMS CLICKS =========================================**/
        private fun navItemClickListener(
            binding: ActivityMainBinding,
            homeViewModel: HomeViewModel,
            adapter: HomeRecyclerAdapter
        ) {
            binding.homeNavigationView.setNavigationItemSelectedListener {

                when (it.itemId) {
                    R.id.nav_notes -> {
                        homeViewModel.isNotesSection = true
                        if (!binding.homeToolbar.title.equals("Notes")) {
                            val archivesSize = homeViewModel.displayNotesList.size

                            homeViewModel.switchToNotes()
                            adapter.notifyItemRangeRemoved(0, archivesSize)
                            adapter.notifyItemRangeInserted(0, homeViewModel.displayNotesList.size)

                            println(homeViewModel.displayNotesList.size)

                            binding.homeToolbar.title = "Notes"
                            binding.homeFab.visibility = View.VISIBLE
                        }
                    }
                    R.id.nav_archives -> {
                        homeViewModel.isNotesSection = false
                        if (!binding.homeToolbar.title.equals("Archives")) {
                            val notesSize = homeViewModel.displayNotesList.size

                            homeViewModel.switchToArchives()
                            adapter.notifyItemRangeRemoved(0, notesSize)
                            adapter.notifyItemRangeInserted(0, homeViewModel.displayNotesList.size)

                            binding.homeToolbar.title = "Archives"
                            binding.homeFab.visibility = View.GONE
                        }
                    }
                }

                if (it.itemId != R.id.action_theme)
                    binding.homeDrawer.closeDrawer(GravityCompat.START)

                true
            }
        }

    } //End Companion Object
}