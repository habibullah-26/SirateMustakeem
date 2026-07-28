package com.habib.siratemustakeem.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.models.TranslationOption

class TranslationAdapter(
    private val options: List<TranslationOption>,
    private val onClick: (TranslationOption) -> Unit
) : RecyclerView.Adapter<TranslationAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEnglish: TextView = itemView.findViewById(R.id.tvEnglish)
        val tvUrdu: TextView = itemView.findViewById(R.id.tvUrdu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item_dua, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options[position]
        holder.tvEnglish.text = option.scholarName
        holder.tvUrdu.text = option.label
        holder.itemView.setOnClickListener { onClick(option) }
    }

    override fun getItemCount(): Int = options.size
}
