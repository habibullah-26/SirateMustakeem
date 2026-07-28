package com.habib.siratemustakeem.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.adapters.ReadAdapter
import com.habib.siratemustakeem.adapters.ReadListItem
import com.habib.siratemustakeem.databinding.ActivityMainNewBinding
import com.habib.siratemustakeem.models.AyahDisplay
import com.habib.siratemustakeem.models.QuranConstants
import com.habib.siratemustakeem.models.SurahMeta
import com.habib.siratemustakeem.network.RetrofitClient
import com.habib.siratemustakeem.utils.QuranTypographyUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class QuranReadActivity : AppCompatActivity() {

    private var binding: ActivityMainNewBinding? = null
    private var progressBar: ProgressBar? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentlyPlayingAyahPosition: Int = -1
    private var adapter: ReadAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_new)

        val mode = intent.getStringExtra(QuranConstants.EXTRA_QURAN_MODE) ?: QuranConstants.MODE_TILAWAT
        val browseMode = intent.getStringExtra(QuranConstants.EXTRA_BROWSE_MODE) ?: QuranConstants.BROWSE_MODE_SURAH
        val number = intent.getIntExtra(QuranConstants.EXTRA_SURAH_OR_JUZ_NUMBER, 1)
        val title = intent.getStringExtra(QuranConstants.EXTRA_TITLE) ?: ""
        // Only meaningful when mode == MODE_TAFSEER; null in Tilawat mode (Arabic + audio only).
        val translationEdition = intent.getStringExtra(QuranConstants.EXTRA_TRANSLATION_EDITION)
            .takeIf { mode == QuranConstants.MODE_TAFSEER }

        binding?.toplayout?.tvTitle?.text = title
        binding?.toplayout?.backImage?.setOnClickListener { finish() }

        binding?.recyclerView?.layoutManager = LinearLayoutManager(this)
        addProgressBar()
        loadAyahs(browseMode, number, translationEdition)
    }

    private fun addProgressBar() {
        val root = binding?.root as? android.widget.LinearLayout ?: return
        val params = android.widget.LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.CENTER_HORIZONTAL; topMargin = 48 }
        progressBar = ProgressBar(this)
        root.addView(progressBar, params)
    }

    private fun loadAyahs(browseMode: String, number: Int, translationEdition: String?) {
        progressBar?.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                coroutineScope {
                    val metaDeferred = async { RetrofitClient.quranApi.getAllSurahs() }

                    val ayahs: List<AyahDisplay> = if (browseMode == QuranConstants.BROWSE_MODE_JUZ) {
                        loadJuzAyahs(number, translationEdition)
                    } else {
                        loadSurahAyahs(number, translationEdition)
                    }
                    val metaMap = metaDeferred.await().data.associateBy { it.number }
                    val rukuBaselines = ayahs.map { it.surahNumber }.distinct().associateWith { surahNum ->
                        async { com.habib.siratemustakeem.network.RukuBaselineCache.getBaseline(surahNum) }
                    }.mapValues { it.value.await() }

                    progressBar?.visibility = View.GONE
                    val listItems = buildListItems(ayahs, browseMode, metaMap, rukuBaselines)
                    adapter = ReadAdapter(listItems) { ayahPosition, ayah -> toggleAudio(ayahPosition, ayah) }
                    binding?.recyclerView?.adapter = adapter
                }
            } catch (e: Exception) {
                progressBar?.visibility = View.GONE
                Toast.makeText(
                    this@QuranReadActivity,
                    getString(R.string.quran_load_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /** Groups the flat ayah list by surah and inserts a header (+ Bismillah where relevant)
     *  before each surah's ayahs, so the translation screen shows the same Surah/Ruku/Para
     *  info as the Tilawat screen. */
    private fun buildListItems(
        ayahs: List<AyahDisplay>,
        browseMode: String,
        metaMap: Map<Int, SurahMeta>,
        rukuBaselines: Map<Int, Int>
    ): List<ReadListItem> {
        val result = mutableListOf<ReadListItem>()
        var currentSurah = -1
        var isFirstGroup = true

        for (ayah in ayahs) {
            if (ayah.surahNumber != currentSurah) {
                currentSurah = ayah.surahNumber
                val meta = metaMap[currentSurah]
                val ayahsOfThisSurah = ayahs.filter { it.surahNumber == currentSurah }
                val baseline = rukuBaselines[currentSurah] ?: 1
                val rukus = ayahsOfThisSurah.mapNotNull { it.ruku?.let { raw -> raw - baseline + 1 } }

                val startsAtAyahOne = ayahsOfThisSurah.firstOrNull()?.numberInSurah == 1
                val forceBismillahAtParaTop = browseMode == QuranConstants.BROWSE_MODE_JUZ && isFirstGroup
                val showBismillah = currentSurah != 9 && currentSurah != 1 &&
                    (startsAtAyahOne || forceBismillahAtParaTop)

                result.add(
                    ReadListItem.Header(
                        surahNumber = currentSurah,
                        arabicName = meta?.name ?: ayah.surahName,
                        isMeccan = meta?.revelationType?.equals("Meccan", ignoreCase = true) ?: true,
                        ayahCount = meta?.numberOfAyahs ?: ayahsOfThisSurah.size,
                        rukuMin = rukus.minOrNull(),
                        rukuMax = rukus.maxOrNull(),
                        juz = ayahsOfThisSurah.firstOrNull()?.juz,
                        showBismillah = showBismillah
                    )
                )

                // The Uthmani text embeds Bismillah as a literal prefix inside ayah 1's own text
                // for most surahs — strip it here since it's already shown in the header above,
                // otherwise it renders twice.
                if (showBismillah && startsAtAyahOne) {
                    val cleaned = QuranTypographyUtils.stripLeadingBismillahIfPresent(ayah.arabicText)
                    result.add(ReadListItem.Ayah(ayah.copy(arabicText = cleaned)))
                    isFirstGroup = false
                    continue
                }
                isFirstGroup = false
            }
            result.add(ReadListItem.Ayah(ayah))
        }
        return result
    }

    // Surah: the combined /editions/a,b,c endpoint works reliably here.
    // Editions requested are always in this order: Arabic, [Urdu translation], Audio.
    private suspend fun loadSurahAyahs(surahNumber: Int, translationEdition: String?): List<AyahDisplay> {
        val editions = listOfNotNull("quran-uthmani", translationEdition, "ar.alafasy").joinToString(",")
        val response = RetrofitClient.quranApi.getSurahByEditions(surahNumber, editions)

        val arabicAyahs = response.data.getOrNull(0)?.ayahs.orEmpty()
        val urduAyahs = if (translationEdition != null) response.data.getOrNull(1)?.ayahs.orEmpty() else emptyList()
        val audioAyahs = (if (translationEdition != null) response.data.getOrNull(2) else response.data.getOrNull(1))
            ?.ayahs.orEmpty()

        return arabicAyahs.mapIndexed { index, arabic ->
            AyahDisplay(
                numberInSurah = arabic.numberInSurah,
                surahNumber = surahNumber,
                surahName = arabic.surah?.englishName ?: "",
                arabicText = arabic.text,
                urduText = urduAyahs.getOrNull(index)?.text ?: "",
                audioUrl = audioAyahs.getOrNull(index)?.audio,
                juz = arabic.juz,
                ruku = arabic.ruku
            )
        }
    }

    // Juz: the combined /editions/a,b,c endpoint is not reliably supported for /juz/,
    // so fetch each needed edition separately (in parallel) and merge by index instead.
    private suspend fun loadJuzAyahs(juzNumber: Int, translationEdition: String?): List<AyahDisplay> = coroutineScope {
        val arabicDeferred = async { RetrofitClient.quranApi.getJuzByEdition(juzNumber, "quran-uthmani") }
        val audioDeferred = async { RetrofitClient.quranApi.getJuzByEdition(juzNumber, "ar.alafasy") }
        val urduDeferred = translationEdition?.let { edition ->
            async { RetrofitClient.quranApi.getJuzByEdition(juzNumber, edition) }
        }

        val arabicAyahs = arabicDeferred.await().data.ayahs
        val audioAyahs = audioDeferred.await().data.ayahs
        val urduAyahs = urduDeferred?.await()?.data?.ayahs.orEmpty()

        arabicAyahs.mapIndexed { index, arabic ->
            AyahDisplay(
                numberInSurah = arabic.numberInSurah,
                surahNumber = arabic.surah?.number ?: 0,
                surahName = arabic.surah?.englishName ?: "",
                arabicText = arabic.text,
                urduText = urduAyahs.getOrNull(index)?.text ?: "",
                audioUrl = audioAyahs.getOrNull(index)?.audio,
                juz = arabic.juz,
                ruku = arabic.ruku
            )
        }
    }

    private fun toggleAudio(ayahPosition: Int, ayah: AyahDisplay) {
        val url = ayah.audioUrl ?: return

        if (currentlyPlayingAyahPosition == ayahPosition) {
            stopAudio()
            return
        }

        stopAudio()
        currentlyPlayingAyahPosition = ayahPosition
        adapter?.setPlayingAyahPosition(ayahPosition)

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener { it.start() }
                setOnCompletionListener { stopAudio() }
                setOnErrorListener { _, _, _ -> stopAudio(); true }
                prepareAsync()
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.audio_play_error), Toast.LENGTH_SHORT).show()
            stopAudio()
        }
    }

    private fun stopAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentlyPlayingAyahPosition = -1
        adapter?.setPlayingAyahPosition(-1)
    }

    override fun onDestroy() {
        stopAudio()
        super.onDestroy()
    }
}
