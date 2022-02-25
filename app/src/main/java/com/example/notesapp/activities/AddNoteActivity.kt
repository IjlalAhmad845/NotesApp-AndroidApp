package com.example.notesapp.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.notesapp.R
import com.example.notesapp.databinding.ActivityAddNoteBinding
import com.example.notesapp.viewModels.HomeViewModel

class AddNoteActivity : AppCompatActivity() {
    companion object {
        var SEND_BACK_TITLE_KEY = "com.example.notesapp.activities.sendTitleBack"
        var SEND_BACK_BODY_KEY = "com.example.notesapp.activities.sendBodyBack"
        var SEND_BACK_INDEX_KEY = "com.example.notesapp.activities.sendIndexBack"
    }

    private lateinit var binding: ActivityAddNoteBinding
    private var cardIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note)


        getNote()
        binding.noteBackButton.setOnClickListener {
            sendDataBack()
        }
    }

    private fun getNote() {

        //for new note this intent will be empty
        val data = intent
        val noteTitle = data.getStringExtra(HomeViewModel.NOTE_TITLE_KEY)
        val noteBody = data.getStringExtra(HomeViewModel.NOTE_BODY_KEY)
        val position = data.getIntExtra(HomeViewModel.NOTE_INDEX_KEY, -1)

        binding.noteHeaderTextView.setText(noteTitle)
        binding.noteBodyTextView.setText(noteBody)

        cardIndex = position
    }

    /**================================== METHOD FOR SENDING DATA BACK TO HOME ACTIVITY ==================================**/
    private fun sendDataBack() {
        val title = binding.noteHeaderTextView.text.toString()
        val body = binding.noteBodyTextView.text.toString()

        val data = Intent()
        data.putExtra(SEND_BACK_TITLE_KEY, title)
        data.putExtra(SEND_BACK_BODY_KEY, body)

        if (cardIndex != -1)
            data.putExtra(SEND_BACK_INDEX_KEY, cardIndex)

        setResult(Activity.RESULT_OK, data)
        super.onBackPressed()
    }

    /**======================================================= ON BACK PRESSED =======================================================**/
    override fun onBackPressed() {
        sendDataBack()
    }
}