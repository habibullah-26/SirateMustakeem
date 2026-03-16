package com.habib.siratemustakeem.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.databinding.ActivityPdfviewBinding

class PdfViewActivity : AppCompatActivity() {
    private var binding: ActivityPdfviewBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pdfview)
    }
}