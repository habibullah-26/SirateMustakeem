package com.habib.siratemustakeem.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.databinding.ActivityQuranHomeBinding
import com.habib.siratemustakeem.models.QuranConstants

class QuranHomeActivity : AppCompatActivity() {

    private var binding: ActivityQuranHomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quran_home)

        binding?.toplayout?.tvTitle?.text = getString(R.string.title_quran_recite)
        binding?.toplayout?.backImage?.setOnClickListener { finish() }

        // Tilawat: straight to Surah/Para choice, Arabic + audio only, no translation.
        binding?.cardTilawat?.setOnClickListener {
            val intent = Intent(this, QuranBrowseActivity::class.java)
            intent.putExtra(QuranConstants.EXTRA_QURAN_MODE, QuranConstants.MODE_TILAWAT)
            startActivity(intent)
        }

        // Tafseer: pick a translation first, then Surah/Para choice.
        binding?.cardTafseer?.setOnClickListener {
            startActivity(Intent(this, TafseerSelectionActivity::class.java))
        }
    }
}
