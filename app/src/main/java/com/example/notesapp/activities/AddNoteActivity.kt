package com.example.notesapp.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.notesapp.R
import com.example.notesapp.databinding.ActivityAddNoteBinding

class AddNoteActivity : AppCompatActivity() {
    companion object{
        var SEND_BACK_TITLE_KEY="com.example.notesapp.activities.title_key"
        var SEND_BACK_BODY_KEY="com.example.notesapp.activities.body_key"
    }
    private lateinit var binding: ActivityAddNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note)

        binding.noteBackButton.setOnClickListener {
            sendDataBack()
        }
    }

    private fun sendDataBack() {
        val title = binding.noteHeaderTextView.text.toString()
        val body = binding.noteBodyTextView.text.toString()

        val data = Intent()
        data.putExtra(SEND_BACK_TITLE_KEY, title)
        data.putExtra(SEND_BACK_BODY_KEY,body)


        setResult(Activity.RESULT_OK, data)

        super.onBackPressed()

    }

    override fun onBackPressed() {
        sendDataBack()
    }
}