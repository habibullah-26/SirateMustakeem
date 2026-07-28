package com.habib.siratemustakeem.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.models.AyahDisplay
import com.habib.siratemustakeem.utils.QuranTypographyUtils

sealed class ReadListItem {
    data class Header(
        val surahNumber: Int,
        val arabicName: String,
        val isMeccan: Boolean,
        val ayahCount: Int,
        val rukuMin: Int?,
        val rukuMax: Int?,
        val juz: Int?,
        val showBismillah: Boolean
    ) : ReadListItem()

    data class Ayah(val ayah: AyahDisplay) : ReadListItem()
}

class ReadAdapter(
    private val items: List<ReadListItem>,
    private val onPlayClick: (Int, AyahDisplay) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var playingAyahPosition: Int = -1

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_AYAH = 1
    }

    fun setPlayingAyahPosition(position: Int) {
        val previous = playingAyahPosition
        playingAyahPosition = position
        if (previous != -1) notifyItemChanged(previous)
        if (position != -1) notifyItemChanged(position)
    }

    override fun getItemViewType(position: Int): Int =
        if (items[position] is ReadListItem.Header) VIEW_TYPE_HEADER else VIEW_TYPE_AYAH

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvSegHeaderTitle)
        val tvMeta: TextView = itemView.findViewById(R.id.tvSegHeaderMeta)
        val tvParaTag: TextView = itemView.findViewById(R.id.tvSegParaTag)
        val tvBismillah: TextView = itemView.findViewById(R.id.tvBismillah)
    }

    inner class AyahViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ayahNumber: TextView = itemView.findViewById(R.id.tvAyahNumber)
        val arabicText: TextView = itemView.findViewById(R.id.tvAyahArabic)
        val urduText: TextView = itemView.findViewById(R.id.tvAyahUrdu)
        val playButton: ImageButton = itemView.findViewById(R.id.btnPlayAyah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_quran_read_header, parent, false))
        } else {
            AyahViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_item_ayah, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ReadListItem.Header -> bindHeader(holder as HeaderViewHolder, item)
            is ReadListItem.Ayah -> bindAyah(holder as AyahViewHolder, item.ayah, position)
        }
    }

    private fun bindHeader(holder: HeaderViewHolder, item: ReadListItem.Header) {
        val context = holder.itemView.context
        holder.tvTitle.text = "${QuranTypographyUtils.toArabicIndicDigits(item.surahNumber)} - ${item.arabicName}"

        val revelationLabel = context.getString(if (item.isMeccan) R.string.label_meccan else R.string.label_medinan)
        val ayahCountLabel = "${context.getString(R.string.label_ayahs_count)}: ${QuranTypographyUtils.toArabicIndicDigits(item.ayahCount)}"
        val rukuLabel = if (item.rukuMin != null && item.rukuMax != null) {
            val prefix = context.getString(R.string.label_ruku)
            if (item.rukuMin == item.rukuMax) {
                "$prefix: ${QuranTypographyUtils.toArabicIndicDigits(item.rukuMin)}"
            } else {
                "$prefix: ${QuranTypographyUtils.toArabicIndicDigits(item.rukuMin)} ${context.getString(R.string.label_ruku_to)} ${QuranTypographyUtils.toArabicIndicDigits(item.rukuMax)}"
            }
        } else null
        holder.tvMeta.text = "$revelationLabel  •  $ayahCountLabel"

        val paraLabel = item.juz?.let { "${context.getString(R.string.para_label_prefix)}: ${QuranTypographyUtils.toArabicIndicDigits(it)}" }
        val locationLine = listOfNotNull(rukuLabel, paraLabel).joinToString("   |   ")
        if (locationLine.isNotBlank()) {
            holder.tvParaTag.visibility = View.VISIBLE
            holder.tvParaTag.text = locationLine
        } else {
            holder.tvParaTag.visibility = View.GONE
        }

        if (item.showBismillah) {
            holder.tvBismillah.visibility = View.VISIBLE
            holder.tvBismillah.text = QuranTypographyUtils.BISMILLAH
        } else {
            holder.tvBismillah.visibility = View.GONE
        }
    }

    private fun bindAyah(holder: AyahViewHolder, ayah: AyahDisplay, position: Int) {
        holder.ayahNumber.text = QuranTypographyUtils.toArabicIndicDigits(ayah.numberInSurah)
        val arabicBuilder = android.text.SpannableStringBuilder()
        QuranTypographyUtils.appendAyahTextWithWaqfStyling(arabicBuilder, ayah.arabicText, android.graphics.Color.parseColor("#B71C1C"))
        holder.arabicText.text = arabicBuilder
        holder.urduText.text = ayah.urduText
        holder.urduText.visibility = if (ayah.urduText.isBlank()) View.GONE else View.VISIBLE

        val isPlaying = position == playingAyahPosition
        holder.playButton.setImageResource(
            if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        )
        holder.playButton.setOnClickListener { onPlayClick(position, ayah) }
    }

    override fun getItemCount(): Int = items.size
}
