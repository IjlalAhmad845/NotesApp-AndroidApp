package com.example.notesapp.controllers

import android.app.Activity
import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import com.example.notesapp.R
import com.example.notesapp.adapters.HomeRecyclerAdapter
import com.example.notesapp.database.Preferences
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.viewModels.HomeViewModel
import com.google.android.material.switchmaterial.SwitchMaterial


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
            navItemClickListener(context, binding, homeViewModel, adapter)
        }

        /**======================================= FUNCTION FOR HANDLING NAV ITEMS CLICKS =========================================**/
        private fun navItemClickListener(
            context: Context,
            binding: ActivityMainBinding,
            homeViewModel: HomeViewModel,
            adapter: HomeRecyclerAdapter
        ) {
            //Handling dark theme menu item
            //Handling dark theme menu item
            val switchMaterial: SwitchMaterial = setDarkThemeMenuItem(
                context,
                binding.homeNavigationView.menu.findItem(R.id.action_theme)
            )

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
                    R.id.action_theme -> {
                        switchMaterial.isChecked = !switchMaterial.isChecked
                    }
                }

                if (it.itemId != R.id.action_theme)
                    binding.homeDrawer.closeDrawer(GravityCompat.START)

                true
            }
        }

        /**
         * ======================================= METHOD FOR HANDLING DARK THEME MENU ITEM ==================================
         */
        private fun setDarkThemeMenuItem(context: Context?, menuItem: MenuItem): SwitchMaterial {
            menuItem.actionView = SwitchMaterial(context!!)
            val switchMaterial = menuItem.actionView as SwitchMaterial

            //getting last saved state of dark theme
            val isDarkEnabled = Preferences.getThemeState(context)

            //applying changes
            switchMaterial.isChecked = isDarkEnabled
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )

            //click listener for dark theme switch
            switchMaterial.setOnCheckedChangeListener { _, b: Boolean ->
                AppCompatDelegate.setDefaultNightMode(
                    if (b) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )

                //saving theme state in Shared Preferences
                Preferences.saveThemeState(context, b)
            }
            return switchMaterial
        }

    } //End Companion Object
}