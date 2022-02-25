package com.example.notesapp.adapters

import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.dataModels.Notes

class HomeRecyclerAdapter(private var notesList: MutableList<Notes>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notesList[position]

        holder.bind(holder, note)
    }

    override fun getItemCount() = notesList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var noteCard = itemView.findViewById<ConstraintLayout>(R.id.note_card)
        private var noteHeader = itemView.findViewById<TextView>(R.id.note_header_text_view)
        private var noteBody = itemView.findViewById<TextView>(R.id.note_body_text_view)
        private var noteSelected = itemView.findViewById<ImageView>(R.id.note_selected_icon)

        fun bind(holder: ViewHolder, note: Notes) {

            holder.noteHeader.text = note.head
            holder.noteBody.text = note.body
            holder.noteSelected.visibility =
                if (note.isSelected) {
                    holder.noteCard.setBackgroundResource(R.drawable.note_selected_boundary)
                    View.VISIBLE
                } else {
                    holder.noteCard.setBackgroundResource(android.R.color.transparent)
                    View.GONE
                }

        }

    }
}