package com.example.notesapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
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
    private var color: Int = 0

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

    private fun colorPicker() {
        val view = LayoutInflater.from(this).inflate(R.layout.color_palette, null)

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Note Picker")
            .setView(view)
            .show()

        view.findViewById<CardView>(R.id.cardView1)
            .setOnClickListener { color = 1; dialog.dismiss() }
        view.findViewById<CardView>(R.id.cardView2)
            .setOnClickListener { color = 2; dialog.dismiss() }
        view.findViewById<CardView>(R.id.cardView3)
            .setOnClickListener { color = 3; dialog.dismiss() }
        view.findViewById<CardView>(R.id.cardView4)
            .setOnClickListener { color = 4; dialog.dismiss() }
        view.findViewById<CardView>(R.id.cardView5)
            .setOnClickListener { color = 5; dialog.dismiss() }
        view.findViewById<CardView>(R.id.cardView6)
            .setOnClickListener { color = 6; dialog.dismiss() }
        view.findViewById<CardView>(R.id.cardView7)
            .setOnClickListener { color = 7; dialog.dismiss() }
        view.findViewById<CardView>(R.id.cardView8)
            .setOnClickListener { color = 8; dialog.dismiss() }
        view.findViewById<CardView>(R.id.cardView9)
            .setOnClickListener { color = 9; dialog.dismiss() }
        view.findViewById<CardView>(R.id.cardView10)
            .setOnClickListener { color = 10; dialog.dismiss() }
        view.findViewById<CardView>(R.id.cardView11)
            .setOnClickListener { color = 11; dialog.dismiss() }
        view.findViewById<CardView>(R.id.cardView12)
            .setOnClickListener { color = 12; dialog.dismiss() }
        view.findViewById<CardView>(R.id.cardView13)
            .setOnClickListener { color = 13; dialog.dismiss() }
        view.findViewById<CardView>(R.id.cardView14)
            .setOnClickListener { color = 14; dialog.dismiss() }
        view.findViewById<CardView>(R.id.cardView15)
            .setOnClickListener { color = 15; dialog.dismiss() }
    }

    /**======================================================= ON BACK PRESSED =======================================================**/
    override fun onBackPressed() {
        sendDataBack(0)
    }
}