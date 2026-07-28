package com.habib.siratemustakeem.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.models.QuranConstants

/**
 * Styled to match the Kalma list (row_item_dua.xml). Shows the para number
 * on the English line and its traditional starting-word name (Urdu/Arabic
 * script) on the Urdu line.
 */
class ParaAdapter(
    private val paraNumbers: List<Int>,
    private val onClick: (Int, String) -> Unit
) : RecyclerView.Adapter<ParaAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEnglish: TextView = itemView.findViewById(R.id.tvEnglish)
        val tvUrdu: TextView = itemView.findViewById(R.id.tvUrdu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item_dua, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paraNumber = paraNumbers[position]
        val paraName = QuranConstants.paraNames.getOrElse(paraNumber - 1) { "" }
        holder.tvEnglish.text = "Para $paraNumber"
        holder.tvUrdu.text = paraName
        holder.itemView.setOnClickListener { onClick(paraNumber, paraName) }
    }

    override fun getItemCount(): Int = paraNumbers.size
}
