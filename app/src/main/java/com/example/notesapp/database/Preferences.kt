package com.example.notesapp.database

import android.content.Context
import android.content.SharedPreferences


class Preferences {
    companion object{
        private const val SHARED_PREFERENCE_FILE = "SettingsData"
        private lateinit var sharedPreferences: SharedPreferences
        /**
         * ========================================== FUNCTION FOR SAVING APP'S THEME STATE ========================================
         */
        fun saveThemeState(context: Context, isDarkEnabled: Boolean) {
            sharedPreferences =
                context.getSharedPreferences(SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putBoolean("ThemeState", isDarkEnabled)
            editor.apply()
        }

        /**
         * =========================================== FUNCTION FOR GETTING APP'S THEME STATE =======================================
         */
        fun getThemeState(context: Context): Boolean {
            sharedPreferences =
                context.getSharedPreferences(SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("ThemeState", false)
        }
    }

}