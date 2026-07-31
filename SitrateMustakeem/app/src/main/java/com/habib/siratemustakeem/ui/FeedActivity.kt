package com.habib.siratemustakeem.ui

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
import com.habib.siratemustakeem.databinding.ActivityMainNewBinding
import com.habib.siratemustakeem.hadith.FeedHadith
import com.habib.siratemustakeem.hadith.HadithFeedAdapter
import com.habib.siratemustakeem.hadith.HadithRetrofitClient
import kotlinx.coroutines.launch

class FeedActivity : AppCompatActivity() {

    private var binding: ActivityMainNewBinding? = null
    private var progressBar: ProgressBar? = null

    // Candidate Urdu editions to try, in order — some editions/sections occasionally 404,
    // so we fall back through this list rather than showing a blank feed.
    private val editionCandidates = listOf(
        "urd-bukhari" to "صحیح بخاری",
        "urd-muslim" to "صحیح مسلم",
        "urd-abudawud" to "سنن ابو داؤد",
        "urd-tirmidhi" to "جامع ترمذی"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_new)

        binding?.toplayout?.tvTitle?.text = getString(R.string.title_feed)
        binding?.toplayout?.backImage?.setOnClickListener { finish() }
        binding?.recyclerView?.layoutManager = LinearLayoutManager(this)
        addProgressBar()

        loadFeed()
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

    private fun loadFeed() {
        progressBar?.visibility = View.VISIBLE

        lifecycleScope.launch {
            val results = mutableListOf<FeedHadith>()

            // Pull a handful of small random sections across a couple of editions for variety.
            val shuffled = editionCandidates.shuffled()
            for ((edition, label) in shuffled) {
                if (results.size >= 15) break
                for (sectionNo in (1..8).shuffled().take(3)) {
                    try {
                        val response = HadithRetrofitClient.api.getSection(edition, sectionNo)
                        response.hadiths.take(4).forEach { dto ->
                            results.add(FeedHadith(text = dto.text, bookLabel = label, hadithNumber = dto.hadithnumber))
                        }
                    } catch (e: Exception) {
                        // this edition/section combo failed — just try the next one
                    }
                }
            }

            progressBar?.visibility = View.GONE
            if (results.isEmpty()) {
                Toast.makeText(this@FeedActivity, getString(R.string.quran_load_error), Toast.LENGTH_SHORT).show()
            } else {
                binding?.recyclerView?.adapter = HadithFeedAdapter(results.shuffled())
            }
        }
    }
}
