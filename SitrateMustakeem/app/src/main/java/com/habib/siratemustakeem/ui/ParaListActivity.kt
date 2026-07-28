package com.habib.siratemustakeem.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.adapters.ParaAdapter
import com.habib.siratemustakeem.databinding.ActivityMainNewBinding
import com.habib.siratemustakeem.models.QuranConstants

class ParaListActivity : AppCompatActivity() {

    private var binding: ActivityMainNewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_new)

        val mode = intent.getStringExtra(QuranConstants.EXTRA_QURAN_MODE) ?: QuranConstants.MODE_TILAWAT
        val translationEdition = intent.getStringExtra(QuranConstants.EXTRA_TRANSLATION_EDITION)
        val translationLabel = intent.getStringExtra(QuranConstants.EXTRA_TRANSLATION_LABEL)

        binding?.toplayout?.tvTitle?.text = getString(R.string.title_by_para)
        binding?.toplayout?.backImage?.setOnClickListener { finish() }

        binding?.recyclerView?.layoutManager = LinearLayoutManager(this)

        // Para/Juz count is fixed at 30 across every standard Mushaf,
        // so no network call is needed just to show this list.
        val paraNumbers = (1..30).toList()

        binding?.recyclerView?.adapter = ParaAdapter(paraNumbers) { paraNumber, paraName ->
            val intent = Intent(this, QuranReadActivity::class.java)
            intent.putExtra(QuranConstants.EXTRA_QURAN_MODE, mode)
            intent.putExtra(QuranConstants.EXTRA_TRANSLATION_EDITION, translationEdition)
            intent.putExtra(QuranConstants.EXTRA_TRANSLATION_LABEL, translationLabel)
            intent.putExtra(QuranConstants.EXTRA_BROWSE_MODE, QuranConstants.BROWSE_MODE_JUZ)
            intent.putExtra(QuranConstants.EXTRA_SURAH_OR_JUZ_NUMBER, paraNumber)
            intent.putExtra(
                QuranConstants.EXTRA_TITLE,
                "${getString(R.string.para_label_prefix)} $paraNumber — $paraName"
            )
            startActivity(intent)
        }
    }
}
