package com.habib.siratemustakeem.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.databinding.ActivitySearchBinding
import com.habib.siratemustakeem.models.QuranConstants
import com.habib.siratemustakeem.models.SurahMeta
import com.habib.siratemustakeem.network.RetrofitClient
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private var binding: ActivitySearchBinding? = null
    private var allSurahs: List<SurahMeta> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)

        binding?.toplayout?.tvTitle?.text = getString(R.string.title_search)
        binding?.toplayout?.backImage?.setOnClickListener { finish() }
        binding?.recyclerView?.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            try {
                allSurahs = RetrofitClient.quranApi.getAllSurahs().data
            } catch (e: Exception) { /* search will just be empty until retried */ }
        }

        binding?.etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) = runSearch(s?.toString().orEmpty())
        })
    }

    private fun runSearch(query: String) {
        if (query.isBlank()) {
            binding?.recyclerView?.adapter = null
            binding?.tvSearchHint?.visibility = View.VISIBLE
            return
        }
        binding?.tvSearchHint?.visibility = View.GONE

        val matches = allSurahs.filter {
            it.englishName.contains(query, ignoreCase = true) ||
                it.name.contains(query) ||
                it.englishNameTranslation.contains(query, ignoreCase = true)
        }
        binding?.recyclerView?.adapter = SearchResultAdapter(matches) { surah ->
            val intent = android.content.Intent(this, QuranTilawatActivity::class.java)
            intent.putExtra(QuranConstants.EXTRA_BROWSE_MODE, QuranConstants.BROWSE_MODE_SURAH)
            intent.putExtra(QuranConstants.EXTRA_SURAH_OR_JUZ_NUMBER, surah.number)
            intent.putExtra(QuranConstants.EXTRA_TITLE, surah.englishName)
            startActivity(intent)
        }
    }

    private class SearchResultAdapter(
        private val items: List<SurahMeta>,
        private val onClick: (SurahMeta) -> Unit
    ) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvEnglish: TextView = itemView.findViewById(R.id.tvEnglish)
            val tvUrdu: TextView = itemView.findViewById(R.id.tvUrdu)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item_dua, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvEnglish.text = "${item.number}. ${item.englishName} — ${item.englishNameTranslation}"
            holder.tvUrdu.text = item.name
            holder.itemView.setOnClickListener { onClick(item) }
        }

        override fun getItemCount(): Int = items.size
    }
}
