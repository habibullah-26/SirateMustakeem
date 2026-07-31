package com.habib.siratemustakeem.hadith

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.habib.siratemustakeem.R

data class FeedHadith(
    val text: String,
    val bookLabel: String,
    val hadithNumber: Int
)

class HadithFeedAdapter(private val items: List<FeedHadith>) :
    RecyclerView.Adapter<HadithFeedAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookName: TextView = itemView.findViewById(R.id.tvHadithBookName)
        val text: TextView = itemView.findViewById(R.id.tvHadithText)
        val reference: TextView = itemView.findViewById(R.id.tvHadithReference)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item_hadith, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bookName.text = item.bookLabel
        holder.text.text = item.text
        holder.reference.text = "حدیث نمبر: ${item.hadithNumber}"
    }

    override fun getItemCount(): Int = items.size
}
