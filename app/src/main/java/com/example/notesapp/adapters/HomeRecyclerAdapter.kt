package com.example.notesapp.adapters

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

class HomeRecyclerAdapter(
    private var notesList: MutableList<Notes>,
    private var cardOnClickInterface: CardOnClickInterface
) :
    RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder>() {

    companion object {
        var selectionMode = false;
        var selectedItems: MutableList<Notes> = mutableListOf()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notesList[position]

        holder.bind(holder, note)
    }

    override fun getItemCount() = notesList.size

    interface CardOnClickInterface {
        fun cardOnClick(position: Int)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var noteCard = itemView.findViewById<ConstraintLayout>(R.id.note_card)!!
        private var noteHeader = itemView.findViewById<TextView>(R.id.item_note_header_text_view)
        private var noteBody = itemView.findViewById<TextView>(R.id.item_note_body_text_view)
        private var noteSelected = itemView.findViewById<ImageView>(R.id.note_selected_icon)!!

        init {
            noteCard.setOnClickListener {
                if (selectionMode) {
                    if (selectedItems.contains(notesList[adapterPosition])) {
                        noteSelected.visibility = View.GONE
                        noteCard.setBackgroundResource(android.R.color.transparent)
                        selectedItems.remove(notesList[adapterPosition])
                    } else {
                        noteSelected.visibility = View.VISIBLE

                        noteCard.setBackgroundResource(R.drawable.note_selected_boundary)
                        selectedItems.add(notesList[adapterPosition])
                    }

                    if (selectedItems.size == 0)
                        selectionMode = false

                } else
                    cardOnClickInterface.cardOnClick(adapterPosition)
            }
            handleLongClick()
        }

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

        private fun handleLongClick() {
            noteCard.setOnLongClickListener {
                selectionMode = true
                if (selectedItems.contains(notesList[adapterPosition])) {
                    noteSelected.visibility = View.GONE
                    noteCard.setBackgroundResource(android.R.color.transparent)
                    selectedItems.remove(notesList[adapterPosition])
                } else {
                    noteSelected.visibility = View.VISIBLE

                    noteCard.setBackgroundResource(R.drawable.note_selected_boundary)
                    selectedItems.add(notesList[adapterPosition])
                }

                if (selectedItems.size == 0)
                    selectionMode = false

                true
            }
        }
    }
}