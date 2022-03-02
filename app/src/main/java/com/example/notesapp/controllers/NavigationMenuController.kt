package com.example.notesapp.controllers

import android.app.Activity
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
            binding.homeToolbar.title = "Notes"

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
                    R.id.action_notes -> {

                        homeViewModel.switchToNotes()
                        adapter.notifyDataSetChanged()

                        println(homeViewModel.displayNotesList.size)

                        binding.homeToolbar.title = "Notes"
                    }
                    R.id.action_archives -> {
                        val notesSize = homeViewModel.displayNotesList.size

                        homeViewModel.switchToArchives()
                        adapter.notifyDataSetChanged()

                        binding.homeToolbar.title = "Archives"
                    }
                }

                if (it.itemId != R.id.action_theme)
                    binding.homeDrawer.closeDrawer(GravityCompat.START)

                true
            }
        }

    } //End Companion Object
}