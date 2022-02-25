package com.example.notesapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.R
import com.example.notesapp.dataModels.Notes
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.viewModels.HomeViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var homeViewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        init()

        binding.homeFab.setOnClickListener {
            startAddNoteActivity()
        }
    }

    private fun init() {
        binding.homeToolbar.title = "Notes"
        binding.homeToolbar.setNavigationIcon(R.drawable.ic_home_menu)
        binding.homeRv.adapter = homeViewModel.adapter
    }

    private var addNoteCallback =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it?.resultCode == Activity.RESULT_OK) {
                val noteTitle = it.data?.getStringExtra(AddNoteActivity.SEND_BACK_TITLE_KEY)
                val noteBody = it.data?.getStringExtra(AddNoteActivity.SEND_BACK_BODY_KEY)

                if (!noteTitle.isNullOrEmpty() || !noteBody.isNullOrEmpty()) {
                    homeViewModel.addNote(Notes(noteTitle!!, noteBody!!, false))
                    homeViewModel.adapter.notifyItemInserted(homeViewModel.notesList.value!!.size)
                }
            }
        }

    private fun startAddNoteActivity() {
        val intent = Intent(this, AddNoteActivity::class.java)
        addNoteCallback.launch(intent)
    }
}