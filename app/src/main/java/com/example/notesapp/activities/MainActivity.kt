package com.example.notesapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.R
import com.example.notesapp.adapters.HomeRecyclerAdapter
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.viewModels.HomeViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var homeViewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        setToolbar()
        initRecyclerView()

        binding.homeFab.setOnClickListener {
            addNote()
        }
    }

    private fun setToolbar() {
        binding.homeToolbar.title = "Notes"
        binding.homeToolbar.setNavigationIcon(R.drawable.ic_home_menu)
    }

    private fun initRecyclerView() {
        val adapter = HomeRecyclerAdapter(homeViewModel.notesList.value!!)
        binding.homeRv.adapter = adapter

        homeViewModel.notesList.observe(this) {
            adapter.notifyDataSetChanged()
        }
    }

    private var addNoteCallback =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            if (it?.resultCode == Activity.RESULT_OK) {
                val noteTitle = it.data?.getStringExtra(AddNoteActivity.SEND_BACK_KEY)
                Toast.makeText(this, noteTitle, Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private fun addNote() {
        val intent = Intent(this, AddNoteActivity::class.java)
        addNoteCallback.launch(intent)
    }
}