package com.example.notesapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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
        private var noteCardLayout =
            itemView.findViewById<ConstraintLayout>(R.id.note_card_layout)!!
        private var noteHeader = itemView.findViewById<TextView>(R.id.item_note_header_text_view)
        private var noteBody = itemView.findViewById<TextView>(R.id.item_note_body_text_view)
        private var noteSelected = itemView.findViewById<ImageView>(R.id.note_selected_icon)!!

        init {
            handleOnClick()
            handleLongClick()
        }

        /**================================================ METHOD FOR BINDING VIEWS ===================================================**/
        fun bind(holder: ViewHolder, note: Notes) {

            val context = holder.noteCard.context
            holder.noteHeader.text = note.head
            holder.noteBody.text = note.body
            holder.noteSelected.visibility =
                if (note.isSelected) {
                    //setting background resource to constraint layout
                    holder.noteCardLayout.setBackgroundResource(R.drawable.note_selected_boundary)
                    holder.noteSelected.setColorFilter(
                        ContextCompat.getColor(
                            holder.noteSelected.context,
                            R.color.note_selected_color
                        )
                    )
                    View.VISIBLE
                } else {
                    //setting background resource to constraint layout
                    holder.noteCardLayout.setBackgroundResource(R.drawable.note_boundary)
                    View.INVISIBLE
                }

            //setting background color to card view
            holder.noteCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    setNoteColor(note)
                )
            )
        }

        /**====================================== FUNCTION FOR SELECTING COLOR FOT NOTE ====================================== **/
        private fun setNoteColor(note: Notes): Int {
            return when (note.color) {
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
                12 -> R.color.cyan
                13 -> R.color.light_purple
                14 -> R.color.red
                else -> R.color.background_color
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