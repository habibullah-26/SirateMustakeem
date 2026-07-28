package com.habib.siratemustakeem.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.databinding.ActivityQuranTilawatBinding
import com.habib.siratemustakeem.models.AyahDto
import com.habib.siratemustakeem.models.QuranConstants
import com.habib.siratemustakeem.models.SurahMeta
import com.habib.siratemustakeem.network.RetrofitClient
import com.habib.siratemustakeem.utils.QuranTypographyUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class QuranTilawatActivity : AppCompatActivity() {

    private data class Segment(
        val surahNumber: Int,
        val ayahs: List<AyahDto>,
        val audioUrls: List<String?>,
        val textView: TextView,
        val playButton: ImageButton,
        val showBismillah: Boolean
    )

    private var binding: ActivityQuranTilawatBinding? = null
    private val segments = mutableListOf<Segment>()
    private var mediaPlayer: MediaPlayer? = null
    private var playingSegmentIndex: Int = -1
    private var playingLocalIndex: Int = -1
    private var autoAdvanceSegmentIndex: Int = -1 // set when "listen to whole surah" is active

    private val markerBitmapCache = mutableMapOf<Int, Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quran_tilawat)

        val browseMode = intent.getStringExtra(QuranConstants.EXTRA_BROWSE_MODE) ?: QuranConstants.BROWSE_MODE_SURAH
        val number = intent.getIntExtra(QuranConstants.EXTRA_SURAH_OR_JUZ_NUMBER, 1)
        val title = intent.getStringExtra(QuranConstants.EXTRA_TITLE) ?: ""

        binding?.toplayout?.tvTitle?.text = title
        binding?.toplayout?.backImage?.setOnClickListener { finish() }

        loadContent(browseMode, number)
    }

    private fun loadContent(browseMode: String, number: Int) {
        binding?.progressBar?.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                coroutineScope {
                    val metaDeferred = async { RetrofitClient.quranApi.getAllSurahs() }

                    val groups: LinkedHashMap<Int, MutableList<AyahDto>> = LinkedHashMap()
                    val audioBySurah: LinkedHashMap<Int, MutableList<String?>> = LinkedHashMap()

                    if (browseMode == QuranConstants.BROWSE_MODE_JUZ) {
                        val arabicDeferred = async { RetrofitClient.quranApi.getJuzByEdition(number, "quran-uthmani") }
                        val audioDeferred = async { RetrofitClient.quranApi.getJuzByEdition(number, "ar.alafasy") }
                        val arabicAyahs = arabicDeferred.await().data.ayahs
                        val audioAyahs = audioDeferred.await().data.ayahs

                        arabicAyahs.forEachIndexed { index, ayah ->
                            val surahNumber = ayah.surah?.number ?: 0
                            groups.getOrPut(surahNumber) { mutableListOf() }.add(ayah)
                            audioBySurah.getOrPut(surahNumber) { mutableListOf() }
                                .add(audioAyahs.getOrNull(index)?.audio)
                        }
                    } else {
                        val response = RetrofitClient.quranApi.getSurahByEditions(number, "quran-uthmani,ar.alafasy")
                        val arabicAyahs = response.data.getOrNull(0)?.ayahs.orEmpty()
                        val audioAyahs = response.data.getOrNull(1)?.ayahs.orEmpty()
                        groups[number] = arabicAyahs.toMutableList()
                        audioBySurah[number] = arabicAyahs.indices.map { audioAyahs.getOrNull(it)?.audio }.toMutableList()
                    }

                    val metaMap = metaDeferred.await().data.associateBy { it.number }
                    val rukuBaselines = groups.keys.associateWith { surahNum ->
                        async { com.habib.siratemustakeem.network.RukuBaselineCache.getBaseline(surahNum) }
                    }.mapValues { it.value.await() }

                    binding?.progressBar?.visibility = View.GONE
                    renderSegments(groups, audioBySurah, metaMap, browseMode, rukuBaselines)
                }
            } catch (e: Exception) {
                binding?.progressBar?.visibility = View.GONE
                Toast.makeText(this@QuranTilawatActivity, getString(R.string.quran_load_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun renderSegments(
        groups: Map<Int, List<AyahDto>>,
        audioBySurah: Map<Int, List<String?>>,
        metaMap: Map<Int, SurahMeta>,
        browseMode: String,
        rukuBaselines: Map<Int, Int>
    ) {
        val container = binding?.segmentsContainer ?: return
        container.removeAllViews()
        segments.clear()

        groups.entries.forEachIndexed { groupIndex, (surahNumber, ayahs) ->
            val meta = metaMap[surahNumber]
            val audioUrls = audioBySurah[surahNumber].orEmpty()

            val segmentView = LayoutInflater.from(this)
                .inflate(R.layout.row_quran_surah_segment, container, false)

            val tvHeaderTitle = segmentView.findViewById<TextView>(R.id.tvSegHeaderTitle)
            val tvHeaderMeta = segmentView.findViewById<TextView>(R.id.tvSegHeaderMeta)
            val tvParaTag = segmentView.findViewById<TextView>(R.id.tvSegParaTag)
            val tvBismillah = segmentView.findViewById<TextView>(R.id.tvBismillah)
            val tvAyahParagraph = segmentView.findViewById<TextView>(R.id.tvAyahParagraph)
            val btnPlaySegment = segmentView.findViewById<ImageButton>(R.id.btnPlaySegment)

            // Arabic name only — deliberately no Latin digits/English name mixed in,
            // since combining scripts in one RTL line causes bidi reordering glitches.
            val arabicName = meta?.name ?: ayahs.firstOrNull()?.surah?.name ?: ""
            val isMeccan = meta?.revelationType?.equals("Meccan", ignoreCase = true) ?: true
            val ayahCount = meta?.numberOfAyahs ?: ayahs.size

            tvHeaderTitle.text = "${QuranTypographyUtils.toArabicIndicDigits(surahNumber)} - $arabicName"

            val baseline = rukuBaselines[surahNumber] ?: 1
            val rukus = ayahs.mapNotNull { it.ruku?.let { raw -> raw - baseline + 1 } }
            val rukuText = if (rukus.isNotEmpty()) {
                val minR = rukus.min()
                val maxR = rukus.max()
                val rukuLabel = getString(R.string.label_ruku)
                if (minR == maxR) {
                    "$rukuLabel: ${QuranTypographyUtils.toArabicIndicDigits(minR)}"
                } else {
                    "$rukuLabel: ${QuranTypographyUtils.toArabicIndicDigits(minR)} ${getString(R.string.label_ruku_to)} ${QuranTypographyUtils.toArabicIndicDigits(maxR)}"
                }
            } else null

            val revelationLabel = if (isMeccan) getString(R.string.label_meccan) else getString(R.string.label_medinan)
            val ayahCountLabel = "${getString(R.string.label_ayahs_count)}: ${QuranTypographyUtils.toArabicIndicDigits(ayahCount)}"
            tvHeaderMeta.text = "$revelationLabel  •  $ayahCountLabel"

            val juzNumber = ayahs.firstOrNull()?.juz
            val paraLabel = juzNumber?.let { "${getString(R.string.para_label_prefix)}: ${QuranTypographyUtils.toArabicIndicDigits(it)}" }
            val locationLine = listOfNotNull(rukuText, paraLabel).joinToString("   |   ")
            if (locationLine.isNotBlank()) {
                tvParaTag.visibility = View.VISIBLE
                tvParaTag.text = locationLine
            } else {
                tvParaTag.visibility = View.GONE
            }

            // Bismillah is recited before every surah except At-Tawbah (9), and is not shown
            // again for Al-Faatiha (1) since it is already ayah 1 there. When browsing by Para,
            // Bismillah is also shown at the very top of the Para regardless of where the first
            // surah segment begins, matching how printed Mushafs open a Para's recitation.
            val startsAtAyahOne = ayahs.firstOrNull()?.numberInSurah == 1
            val isFirstSegmentOfParaView = browseMode == QuranConstants.BROWSE_MODE_JUZ && groupIndex == 0
            val showBismillah = surahNumber != 9 && surahNumber != 1 &&
                (startsAtAyahOne || isFirstSegmentOfParaView)
            tvBismillah.visibility = if (showBismillah) View.VISIBLE else View.GONE
            if (tvBismillah.visibility == View.VISIBLE) {
                tvBismillah.text = QuranTypographyUtils.BISMILLAH
            }

            val segmentIndex = segments.size
            val segment = Segment(surahNumber, ayahs, audioUrls, tvAyahParagraph, btnPlaySegment, showBismillah)
            segments.add(segment)

            tvAyahParagraph.movementMethod = LinkMovementMethod.getInstance()
            tvAyahParagraph.highlightColor = Color.TRANSPARENT
            renderParagraph(segmentIndex)
            updatePlayButtonIcon(segmentIndex)

            btnPlaySegment.setOnClickListener { toggleSegmentListen(segmentIndex) }

            container.addView(segmentView)
        }
    }

    /** Draws a small filled circle with the ayah number (Arabic-Indic digits) — used instead of
     *  relying on the font to render the Unicode "End of Ayah" ligature, which this app's bundled
     *  Quran font does not reliably render, leaving ayahs running together with no visible marker. */
    private fun getMarkerBitmap(numberInSurah: Int): Bitmap {
        markerBitmapCache[numberInSurah]?.let { return it }

        val diameterPx = (resources.displayMetrics.density * 22).toInt()
        val bitmap = Bitmap.createBitmap(diameterPx, diameterPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#2E7D32")
            style = Paint.Style.FILL
        }
        canvas.drawCircle(diameterPx / 2f, diameterPx / 2f, diameterPx / 2f - 1f, circlePaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = diameterPx * 0.46f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        val text = QuranTypographyUtils.toArabicIndicDigits(numberInSurah)
        val textY = diameterPx / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
        canvas.drawText(text, diameterPx / 2f, textY, textPaint)

        markerBitmapCache[numberInSurah] = bitmap
        return bitmap
    }

    /** Rebuilds the spannable text for one segment, applying a highlight to the currently playing ayah (if any) within it. */
    private fun renderParagraph(segmentIndex: Int) {
        val segment = segments.getOrNull(segmentIndex) ?: return
        val builder = SpannableStringBuilder()

        segment.ayahs.forEachIndexed { localIndex, ayah ->
            val ayahStart = builder.length

            // Strip "Bismillah" from ayah 1 if the Bismillah box is already shown
            val displayText = if (localIndex == 0 && segment.showBismillah) {
                ayah.text.stripBismillah()
            } else {
                ayah.text
            }
            QuranTypographyUtils.appendAyahTextWithWaqfStyling(builder, displayText, Color.parseColor("#B71C1C"))
            builder.append(" ")
            val markerStart = builder.length
            builder.append("\uFFFC") // Object Replacement Character — hosts the drawn marker, glued
            // directly to the ayah text above (no space before it) so the marker can never be
            // wrapped onto its own line by itself; the breakable space after it is where the
            // next ayah's line break may occur instead.
            val markerEnd = builder.length
            builder.setSpan(
                ImageSpan(this, getMarkerBitmap(ayah.numberInSurah), ImageSpan.ALIGN_CENTER),
                markerStart, markerEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.append("  ")
            val ayahEnd = builder.length

            val hasAudio = segment.audioUrls.getOrNull(localIndex) != null
            if (hasAudio) {
                builder.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        toggleAudio(segmentIndex, localIndex, autoAdvance = false)
                    }
                    override fun updateDrawState(ds: android.text.TextPaint) {
                        // Intentionally no underline/color change — this is Quran text, not a link.
                    }
                }, ayahStart, ayahEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            if (segmentIndex == playingSegmentIndex && localIndex == playingLocalIndex) {
                builder.setSpan(
                    BackgroundColorSpan(Color.parseColor("#C8E6C9")),
                    ayahStart, ayahEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        segment.textView.text = builder
    }

    private fun toggleSegmentListen(segmentIndex: Int) {
        if (autoAdvanceSegmentIndex == segmentIndex) {
            stopAudio()
            return
        }
        autoAdvanceSegmentIndex = segmentIndex
        val firstPlayableIndex = segments[segmentIndex].audioUrls.indexOfFirst { it != null }
        if (firstPlayableIndex == -1) {
            Toast.makeText(this, getString(R.string.audio_play_error), Toast.LENGTH_SHORT).show()
            autoAdvanceSegmentIndex = -1
            return
        }
        toggleAudio(segmentIndex, firstPlayableIndex, autoAdvance = true)
    }

    private fun toggleAudio(segmentIndex: Int, localIndex: Int, autoAdvance: Boolean) {
        val url = segments.getOrNull(segmentIndex)?.audioUrls?.getOrNull(localIndex) ?: return

        val wasSameAyah = playingSegmentIndex == segmentIndex && playingLocalIndex == localIndex
        val previousSegmentIndex = playingSegmentIndex

        stopAudioInternal()
        if (previousSegmentIndex >= 0) {
            renderParagraph(previousSegmentIndex)
            updatePlayButtonIcon(previousSegmentIndex)
        }

        if (wasSameAyah && !autoAdvance) return

        playingSegmentIndex = segmentIndex
        playingLocalIndex = localIndex
        if (!autoAdvance) autoAdvanceSegmentIndex = -1
        renderParagraph(segmentIndex)
        updatePlayButtonIcon(segmentIndex)

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener { it.start() }
                setOnCompletionListener { onAyahPlaybackComplete(segmentIndex, localIndex) }
                setOnErrorListener { _, _, _ -> stopAudio(); true }
                prepareAsync()
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.audio_play_error), Toast.LENGTH_SHORT).show()
            stopAudio()
        }
    }

    private fun onAyahPlaybackComplete(segmentIndex: Int, localIndex: Int) {
        if (autoAdvanceSegmentIndex == segmentIndex) {
            val nextIndex = segments[segmentIndex].audioUrls
                .drop(localIndex + 1).indexOfFirst { it != null }
                .let { if (it == -1) -1 else it + localIndex + 1 }
            if (nextIndex != -1) {
                toggleAudio(segmentIndex, nextIndex, autoAdvance = true)
                return
            }
            autoAdvanceSegmentIndex = -1
        }
        stopAudio()
    }

    private fun stopAudioInternal() {
        mediaPlayer?.release()
        mediaPlayer = null
        playingSegmentIndex = -1
        playingLocalIndex = -1
    }

    private fun stopAudio() {
        val segmentToRefresh = playingSegmentIndex
        val autoSegment = autoAdvanceSegmentIndex
        autoAdvanceSegmentIndex = -1
        stopAudioInternal()
        if (segmentToRefresh >= 0) {
            renderParagraph(segmentToRefresh)
            updatePlayButtonIcon(segmentToRefresh)
        }
        if (autoSegment >= 0 && autoSegment != segmentToRefresh) updatePlayButtonIcon(autoSegment)
    }

    private fun updatePlayButtonIcon(segmentIndex: Int) {
        val segment = segments.getOrNull(segmentIndex) ?: return
        val isActive = autoAdvanceSegmentIndex == segmentIndex
        segment.playButton.setImageResource(
            if (isActive) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        )
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}

// Extension function to normalize text for consistent comparison
private fun String.normalizeText(): String {
    return this.trim().replace("\uFEFF", "").replace("\u200C", "").replace("ـ", "")
}

private fun String.stripBismillah(): String {
    val bismillahRegex = Regex("^\\s*بِسْمِ\\s*ٱللَّهِ\\s*ٱلرَّحْمَٰنِ\\s*ٱلرَّحِيمِ\\s*")
    return this.replace(bismillahRegex, "").trim()
}
