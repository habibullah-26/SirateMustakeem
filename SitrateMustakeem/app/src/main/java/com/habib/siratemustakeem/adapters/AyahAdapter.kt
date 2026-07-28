package com.habib.siratemustakeem.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.models.AyahDisplay

class AyahAdapter(
    private val ayahs: List<AyahDisplay>,
    private val onPlayClick: (Int, AyahDisplay) -> Unit
) : RecyclerView.Adapter<AyahAdapter.ViewHolder>() {

    private var playingPosition: Int = -1

    fun setPlayingPosition(position: Int) {
        val previous = playingPosition
        playingPosition = position
        if (previous != -1) notifyItemChanged(previous)
        if (position != -1) notifyItemChanged(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ayahNumber: TextView = itemView.findViewById(R.id.tvAyahNumber)
//        val surahName: TextView = itemView.findViewById(R.id.tvAyahSurahName)
        val arabicText: TextView = itemView.findViewById(R.id.tvAyahArabic)
        val urduText: TextView = itemView.findViewById(R.id.tvAyahUrdu)
        val playButton: ImageButton = itemView.findViewById(R.id.btnPlayAyah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item_ayah, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ayah = ayahs[position]
        holder.ayahNumber.text = "Ayah ${ayah.numberInSurah}"
//        holder.surahName.text = ayah.surahName
//        holder.surahName.visibility = if (ayah.surahName.isBlank()) View.GONE else View.VISIBLE
        holder.arabicText.text = ayah.arabicText
        holder.urduText.text = ayah.urduText
        holder.urduText.visibility = if (ayah.urduText.isBlank()) View.GONE else View.VISIBLE

        val isPlaying = position == playingPosition
        holder.playButton.setImageResource(
            if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        )
        holder.playButton.setOnClickListener { onPlayClick(position, ayah) }
    }

    override fun getItemCount(): Int = ayahs.size
}
