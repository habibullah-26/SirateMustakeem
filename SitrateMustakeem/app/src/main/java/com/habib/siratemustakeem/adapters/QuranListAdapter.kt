package com.habib.siratemustakeem.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.models.SurahMeta

/**
 * A single row: either a Surah (with ayah count + Meccan/Medinan) or a Para
 * (number + traditional starting-word name only).
 */
sealed class QuranListItem {
    data class SurahItem(val surah: SurahMeta) : QuranListItem()
    data class ParaItem(val number: Int, val name: String) : QuranListItem()
}

class QuranListAdapter(
    private var items: List<QuranListItem>,
    private val onClick: (QuranListItem) -> Unit
) : RecyclerView.Adapter<QuranListAdapter.ViewHolder>() {

    private val badgeColors = intArrayOf(
        R.color.quran_badge_purple,
        R.color.quran_badge_green,
        R.color.quran_badge_blue,
        R.color.quran_badge_pink
    )

    fun updateItems(newItems: List<QuranListItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val accentStrip: View = itemView.findViewById(R.id.accentStrip)
        val leftMetaContainer: View = itemView.findViewById(R.id.leftMetaContainer)
        val tvAyahCount: TextView = itemView.findViewById(R.id.tvAyahCount)
        val ivRevelationType: ImageView = itemView.findViewById(R.id.ivRevelationType)
        val tvNumberBadge: TextView = itemView.findViewById(R.id.tvNumberBadge)
        val tvArabicName: TextView = itemView.findViewById(R.id.tvArabicName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item_quran_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val colorRes = badgeColors[position % badgeColors.size]
        val color = context.getColor(colorRes)
        holder.accentStrip.setBackgroundColor(color)
        holder.tvNumberBadge.backgroundTintList = android.content.res.ColorStateList.valueOf(color)

        when (val item = items[position]) {
            is QuranListItem.SurahItem -> {
                holder.leftMetaContainer.visibility = View.VISIBLE
                holder.tvAyahCount.text = item.surah.numberOfAyahs.toString()
                val isMeccan = item.surah.revelationType.equals("Meccan", ignoreCase = true)
                holder.ivRevelationType.setImageResource(if (isMeccan) R.drawable.ic_kaaba else R.drawable.ic_masjid_madani)
                holder.tvNumberBadge.text = item.surah.number.toString()
                holder.tvArabicName.text = item.surah.name
                holder.itemView.setOnClickListener { onClick(item) }
            }
            is QuranListItem.ParaItem -> {
                holder.leftMetaContainer.visibility = View.GONE
                holder.tvNumberBadge.text = item.number.toString()
                holder.tvArabicName.text = item.name
                holder.itemView.setOnClickListener { onClick(item) }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
