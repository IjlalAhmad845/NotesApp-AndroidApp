package com.example.notesapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.example.notesapp.R
import com.example.notesapp.databinding.ActivityAddNoteBinding
import com.example.notesapp.viewModels.HomeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddNoteActivity : AppCompatActivity() {
    companion object {
        const val SEND_BACK_TITLE_KEY = "com.example.notesapp.activities.sendTitleBack"
        const val SEND_BACK_BODY_KEY = "com.example.notesapp.activities.sendBodyBack"
        const val SEND_BACK_INDEX_KEY = "com.example.notesapp.activities.sendIndexBack"

        /*
         * 0 for just adding note to display list
         * 1 for archive new adding note
         * 2 for archiving pre made note
        */
        const val SEND_BACK_NOTE_OPERATION_KEY = "com.example.notesapp.activities.sendTypeBack"
    }

    private lateinit var binding: ActivityAddNoteBinding
    private var cardIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note)


        getNote()
        binding.noteBackButton.setOnClickListener { sendDataBack(0) }
        binding.noteColor.setOnClickListener { colorPicker() }
    }

    /**============================================ METHOD FOR GETTING  FROM INTENT ===========================================**/
    private fun getNote() {

        //for new note this intent will be empty
        val data = intent
        val noteTitle = data.getStringExtra(HomeViewModel.NOTE_TITLE_KEY)
        val noteBody = data.getStringExtra(HomeViewModel.NOTE_BODY_KEY)
        val position = data.getIntExtra(HomeViewModel.NOTE_INDEX_KEY, -1)
        val noteType = data.getIntExtra(HomeViewModel.NOTE_TYPE_KEY, -1)

        //and text will be empty for empty intent
        binding.noteHeaderTextView.setText(noteTitle)
        binding.noteBodyTextView.setText(noteBody)
        binding.noteArchiveButton.setImageResource(
            if (noteType == 0) R.drawable.ic_archive_note
            else R.drawable.ic_unarchive_note
        )

        //setting click listener based on note type
        binding.noteArchiveButton.setOnClickListener {
            if (noteType == 0) sendDataBack(1)
            else sendDataBack(2)
        }

        cardIndex = position
    }

    /**================================== METHOD FOR SENDING DATA BACK TO HOME ACTIVITY ==================================**/
    private fun sendDataBack(operation: Int) {
        val title = binding.noteHeaderTextView.text.toString()
        val body = binding.noteBodyTextView.text.toString()

        val data = Intent()
        data.putExtra(SEND_BACK_TITLE_KEY, title)
        data.putExtra(SEND_BACK_BODY_KEY, body)
        //sending which operation should be done to note
        data.putExtra(SEND_BACK_NOTE_OPERATION_KEY, operation)

        if (cardIndex != -1)
            data.putExtra(SEND_BACK_INDEX_KEY, cardIndex)

        setResult(Activity.RESULT_OK, data)
        super.onBackPressed()
    }

    private fun colorPicker()
    {
            MaterialAlertDialogBuilder(this)
                .setTitle("Color Picker")
                .setView(R.layout.color_palette)
                .show()
    }

    /**======================================================= ON BACK PRESSED =======================================================**/
    override fun onBackPressed() {
        sendDataBack(0)
    }
}