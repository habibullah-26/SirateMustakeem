package com.habib.siratemustakeem.ui

import android.content.Intent
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
import com.habib.siratemustakeem.adapters.SurahAdapter
import com.habib.siratemustakeem.databinding.ActivityMainNewBinding
import com.habib.siratemustakeem.models.QuranConstants
import com.habib.siratemustakeem.network.RetrofitClient
import kotlinx.coroutines.launch

class SurahListActivity : AppCompatActivity() {

    private var binding: ActivityMainNewBinding? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_new)

        val mode = intent.getStringExtra(QuranConstants.EXTRA_QURAN_MODE) ?: QuranConstants.MODE_TILAWAT
        val translationEdition = intent.getStringExtra(QuranConstants.EXTRA_TRANSLATION_EDITION)
        val translationLabel = intent.getStringExtra(QuranConstants.EXTRA_TRANSLATION_LABEL)

        binding?.toplayout?.tvTitle?.text = getString(R.string.title_by_surah)
        binding?.toplayout?.backImage?.setOnClickListener { finish() }

        binding?.recyclerView?.layoutManager = LinearLayoutManager(this)
        addProgressBar()
        loadSurahs(mode, translationEdition, translationLabel)
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

    private fun loadSurahs(mode: String, translationEdition: String?, translationLabel: String?) {
        progressBar?.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.quranApi.getAllSurahs()
                progressBar?.visibility = View.GONE

                binding?.recyclerView?.adapter = SurahAdapter(response.data) { surah ->
                    val intent = Intent(this@SurahListActivity, QuranReadActivity::class.java)
                    intent.putExtra(QuranConstants.EXTRA_QURAN_MODE, mode)
                    intent.putExtra(QuranConstants.EXTRA_TRANSLATION_EDITION, translationEdition)
                    intent.putExtra(QuranConstants.EXTRA_TRANSLATION_LABEL, translationLabel)
                    intent.putExtra(QuranConstants.EXTRA_BROWSE_MODE, QuranConstants.BROWSE_MODE_SURAH)
                    intent.putExtra(QuranConstants.EXTRA_SURAH_OR_JUZ_NUMBER, surah.number)
                    intent.putExtra(QuranConstants.EXTRA_TITLE, surah.englishName)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                progressBar?.visibility = View.GONE
                Toast.makeText(
                    this@SurahListActivity,
                    getString(R.string.quran_load_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
