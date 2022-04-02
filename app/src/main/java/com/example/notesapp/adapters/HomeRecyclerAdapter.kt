package com.example.notesapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.dataModels.Notes


class HomeRecyclerAdapter(
    private var notesList: MutableList<Notes>,
    private var cardOnClickInterface: CardOnClickInterface
) :
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

    interface CardOnClickInterface {
        fun cardOnClick(position: Int)
        fun cardLongClick(position: Int)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var noteCard = itemView.findViewById<CardView>(R.id.note_card)!!
        private var noteHeader = itemView.findViewById<TextView>(R.id.item_note_header_text_view)
        private var noteBody = itemView.findViewById<TextView>(R.id.item_note_body_text_view)
        private var noteSelected = itemView.findViewById<ImageView>(R.id.note_selected_icon)!!

        init {
            handleOnClick()
            handleLongClick()
        }

        /**================================================ METHOD FOR BINDING VIEWS ===================================================**/
        fun bind(holder: ViewHolder, note: Notes) {

            holder.noteHeader.text = note.head
            holder.noteBody.text = note.body
            holder.noteSelected.visibility =
                if (note.isSelected) {
                    holder.noteCard.setBackgroundResource(R.drawable.note_selected_boundary)
                    holder.noteSelected.setColorFilter(
                        ContextCompat.getColor(
                            holder.noteSelected.context,
                            R.color.note_selected_color
                        )
                    )
                    View.VISIBLE
                } else {
                    if (note.color == 0)
                        holder.noteCard.setBackgroundResource(R.drawable.note_boundary)
                    else {
                        val unwrappedDrawable =
                            AppCompatResources.getDrawable(
                                holder.noteCard.context,
                                R.drawable.note_background_resource
                            )
                        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
                        DrawableCompat.setTint(
                            wrappedDrawable,
                            ContextCompat.getColor(
                                holder.noteCard.context,
                                when (note.color) {
                                    1 -> R.color.dark_blue
                                    2 -> R.color.orange
                                    3 -> R.color.pink
                                    4 -> R.color.purple
                                    5 -> R.color.violet
                                    6 -> R.color.green
                                    7 -> R.color.olive
                                    8 -> R.color.yellow
                                    9 -> R.color.light_blue
                                    10 -> R.color.light_green
                                    11 -> R.color.light
                                    12 -> R.color.dark
                                    13 -> R.color.light_purple
                                    14 -> R.color.red
                                    else -> android.R.color.transparent
                                }
                            )
                        )
                        holder.noteCard.setBackgroundResource(R.drawable.note_background_resource)
                    }
                    View.INVISIBLE
                }
        }


        /**============================================================== ON CLICK =========================================================**/
        private fun handleOnClick() {
            noteCard.setOnClickListener {
                cardOnClickInterface.cardOnClick(adapterPosition)
            }
        }

        /**=========================================================== LONG CLICK =========================================================**/
        private fun handleLongClick() {
            noteCard.setOnLongClickListener {
                cardOnClickInterface.cardLongClick(adapterPosition)
                true
            }
        }
    }
}