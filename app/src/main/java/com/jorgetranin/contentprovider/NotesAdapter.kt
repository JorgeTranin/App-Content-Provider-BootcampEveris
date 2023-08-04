package com.jorgetranin.contentprovider

import android.annotation.SuppressLint
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jorgetranin.contentprovider.database.NotesDatabaseHelper
import com.jorgetranin.contentprovider.database.NotesDatabaseHelper.Companion.DESCRIPTION_NOTES
import com.jorgetranin.contentprovider.database.NotesDatabaseHelper.Companion.TITLE_NOTES

class NotesAdapter(private val listener: NoteClickListener): RecyclerView.Adapter<NotesViewHolder>() {
    private var mCursor: Cursor? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder =
        NotesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false))

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        mCursor?.moveToPosition(position)
        holder.title.text = mCursor?.getString(mCursor?.getColumnIndex(TITLE_NOTES) as Int)
        holder.description.text = mCursor?.getString(mCursor?.getColumnIndex(DESCRIPTION_NOTES) as Int)
        holder.noteButtonRemove.setOnClickListener {
            mCursor?.moveToPosition(position)
            listener.noteRemoveItem(mCursor)
            notifyDataSetChanged()
        }
        holder.itemView.setOnClickListener {
            listener.noteClickItem(mCursor as Cursor)
        }

    }

    override fun getItemCount(): Int = if (mCursor != null) mCursor?.count as Int else 0

    @SuppressLint("NotifyDataSetChanged")
    fun setCursor(newCursor: Cursor? ){
        mCursor = newCursor
        notifyDataSetChanged()

    }
}


class NotesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val title = itemView.findViewById(R.id.note_title) as TextView
    val description = itemView.findViewById(R.id.note_description) as TextView
    val noteButtonRemove = itemView.findViewById(R.id.btn_note_Remove) as Button
}