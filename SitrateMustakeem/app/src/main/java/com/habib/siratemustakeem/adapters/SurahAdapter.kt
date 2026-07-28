package com.habib.siratemustakeem.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.models.SurahMeta

/**
 * Styled to match the Kalma list (row_item_dua.xml): a centered English line
 * and a centered Urdu/Arabic line, divided by a hairline.
 */
class SurahAdapter(
    private val surahs: List<SurahMeta>,
    private val onClick: (SurahMeta) -> Unit
) : RecyclerView.Adapter<SurahAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEnglish: TextView = itemView.findViewById(R.id.tvEnglish)
        val tvUrdu: TextView = itemView.findViewById(R.id.tvUrdu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item_dua, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val surah = surahs[position]
        holder.tvEnglish.text =
            "${surah.number}. ${surah.englishName} — ${surah.englishNameTranslation} (${surah.numberOfAyahs})"
        holder.tvUrdu.text = surah.name
        holder.itemView.setOnClickListener { onClick(surah) }
    }

    override fun getItemCount(): Int = surahs.size
}
