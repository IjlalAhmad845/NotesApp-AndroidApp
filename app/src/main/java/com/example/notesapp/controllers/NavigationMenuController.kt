package com.example.notesapp.controllers

import android.app.Activity
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.notesapp.R
import com.example.notesapp.databinding.ActivityMainBinding

class NavigationMenuController() {
    companion object {

        fun initNavigationMenu(context: Activity, binding: ActivityMainBinding) {
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
            binding.homeNavigationView.setNavigationItemSelectedListener {
                navigationItemsClickListener(context, it.itemId)

                if (it.itemId != R.id.action_theme)
                    binding.homeDrawer.closeDrawer(GravityCompat.START)

                false
            }

            mToggle.syncState()
            binding.homeToolbar.title = "Notes"
        }


        /**================================== METHOD FOR HANDLING NAV ITEMS CLICKS ================================================**/
        private fun navigationItemsClickListener(context: Activity, item: Int) {

            when (item) {
                R.id.action_notes -> Toast.makeText(context, "notes Clicked", Toast.LENGTH_SHORT)
                    .show()

                R.id.action_archives -> Toast.makeText(
                    context,
                    "archives Clicked",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}