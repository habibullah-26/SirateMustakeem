package com.habib.siratemustakeem.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.adapters.TranslationAdapter
import com.habib.siratemustakeem.databinding.ActivityTafseerSelectionBinding
import com.habib.siratemustakeem.models.QuranConstants

class TafseerSelectionActivity : AppCompatActivity() {

    private var binding: ActivityTafseerSelectionBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tafseer_selection)

        binding?.toplayout?.tvTitle?.text = getString(R.string.title_tafseer)
        binding?.toplayout?.backImage?.setOnClickListener { finish() }

        binding?.recyclerView?.layoutManager = LinearLayoutManager(this)
        binding?.recyclerView?.adapter = TranslationAdapter(QuranConstants.availableTranslations) { option ->
            val intent = Intent(this, QuranBrowseActivity::class.java)
            intent.putExtra(QuranConstants.EXTRA_QURAN_MODE, QuranConstants.MODE_TAFSEER)
            intent.putExtra(QuranConstants.EXTRA_TRANSLATION_EDITION, option.editionIdentifier)
            intent.putExtra(QuranConstants.EXTRA_TRANSLATION_LABEL, option.label)
            startActivity(intent)
        }
    }
}
