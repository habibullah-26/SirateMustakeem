package com.habib.siratemustakeem.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.adapters.QuranListAdapter
import com.habib.siratemustakeem.adapters.QuranListItem
import com.habib.siratemustakeem.databinding.ActivityQuranBrowseBinding
import com.habib.siratemustakeem.models.QuranConstants
import com.habib.siratemustakeem.models.SurahMeta
import com.habib.siratemustakeem.network.RetrofitClient
import kotlinx.coroutines.launch

class QuranBrowseActivity : AppCompatActivity() {

    private var binding: ActivityQuranBrowseBinding? = null
    private var adapter: QuranListAdapter? = null

    private var surahs: List<SurahMeta>? = null // cached after first fetch
    private var showingSurahTab = true

    private var mode = QuranConstants.MODE_TILAWAT
    private var translationEdition: String? = null
    private var translationLabel: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quran_browse)

        mode = intent.getStringExtra(QuranConstants.EXTRA_QURAN_MODE) ?: QuranConstants.MODE_TILAWAT
        translationEdition = intent.getStringExtra(QuranConstants.EXTRA_TRANSLATION_EDITION)
        translationLabel = intent.getStringExtra(QuranConstants.EXTRA_TRANSLATION_LABEL)

        binding?.toplayout?.tvTitle?.text = translationLabel ?: getString(R.string.title_quran_recite)
        binding?.toplayout?.backImage?.setOnClickListener { finish() }

        binding?.recyclerView?.layoutManager = LinearLayoutManager(this)
        adapter = QuranListAdapter(emptyList()) { item -> onItemClicked(item) }
        binding?.recyclerView?.adapter = adapter

        binding?.tabSurah?.setOnClickListener { selectTab(isSurah = true) }
        binding?.tabPara?.setOnClickListener { selectTab(isSurah = false) }

        selectTab(isSurah = true)
    }

    private fun selectTab(isSurah: Boolean) {
        showingSurahTab = isSurah
        val activeTextColor = android.graphics.Color.WHITE
        val inactiveTextColor = android.graphics.Color.parseColor("#2E7D32")

        binding?.tabSurah?.setBackgroundResource(if (isSurah) R.drawable.tab_pill_active_bg else android.R.color.transparent)
        binding?.tabSurah?.setTextColor(if (isSurah) activeTextColor else inactiveTextColor)
        binding?.tabPara?.setBackgroundResource(if (!isSurah) R.drawable.tab_pill_active_bg else android.R.color.transparent)
        binding?.tabPara?.setTextColor(if (!isSurah) activeTextColor else inactiveTextColor)

        if (isSurah) {
            binding?.toplayout?.tvTitle?.text = translationLabel ?: getString(R.string.title_by_surah)
            loadSurahTab()
        } else {
            binding?.toplayout?.tvTitle?.text = translationLabel ?: getString(R.string.title_by_para)
            showParaTab()
        }
    }

    private fun loadSurahTab() {
        val cached = surahs
        if (cached != null) {
            adapter?.updateItems(cached.map { QuranListItem.SurahItem(it) })
            return
        }

        binding?.progressBar?.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.quranApi.getAllSurahs()
                surahs = response.data
                binding?.progressBar?.visibility = View.GONE
                if (showingSurahTab) {
                    adapter?.updateItems(response.data.map { QuranListItem.SurahItem(it) })
                }
            } catch (e: Exception) {
                binding?.progressBar?.visibility = View.GONE
                Toast.makeText(this@QuranBrowseActivity, getString(R.string.quran_load_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showParaTab() {
        // Para/Juz count and names are fixed across every standard Mushaf — no network call needed.
        val items = (1..30).map { number ->
            QuranListItem.ParaItem(number, QuranConstants.juzList.get(number-1).arabicName.toString())
        }
        adapter?.updateItems(items)
    }

    private fun onItemClicked(item: QuranListItem) {
        val targetActivity = if (mode == QuranConstants.MODE_TAFSEER) {
            QuranReadActivity::class.java
        } else {
            QuranTilawatActivity::class.java
        }

        val intent = Intent(this, targetActivity)
        intent.putExtra(QuranConstants.EXTRA_QURAN_MODE, mode)
        intent.putExtra(QuranConstants.EXTRA_TRANSLATION_EDITION, translationEdition)
        intent.putExtra(QuranConstants.EXTRA_TRANSLATION_LABEL, translationLabel)

        when (item) {
            is QuranListItem.SurahItem -> {
                intent.putExtra(QuranConstants.EXTRA_BROWSE_MODE, QuranConstants.BROWSE_MODE_SURAH)
                intent.putExtra(QuranConstants.EXTRA_SURAH_OR_JUZ_NUMBER, item.surah.number)
                intent.putExtra(QuranConstants.EXTRA_TITLE, item.surah.englishName)
            }
            is QuranListItem.ParaItem -> {
                intent.putExtra(QuranConstants.EXTRA_BROWSE_MODE, QuranConstants.BROWSE_MODE_JUZ)
                intent.putExtra(QuranConstants.EXTRA_SURAH_OR_JUZ_NUMBER, item.number)
                intent.putExtra(
                    QuranConstants.EXTRA_TITLE,
                    "${getString(R.string.para_label_prefix)} ${com.habib.siratemustakeem.utils.QuranTypographyUtils.toArabicIndicDigits(item.number)} — ${item.name}"
                )
            }
        }
        startActivity(intent)
    }
}
