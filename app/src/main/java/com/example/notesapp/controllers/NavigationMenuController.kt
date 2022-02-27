package com.example.notesapp.controllers

import com.google.android.material.navigation.NavigationView

class NavigationMenuController {
    companion object {
        val mOnNavigationItemSelectedListener =
            NavigationView.OnNavigationItemSelectedListener { item ->
                false
            }
    }
}