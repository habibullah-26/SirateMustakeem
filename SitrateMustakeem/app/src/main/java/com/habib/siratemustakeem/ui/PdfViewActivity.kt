package com.habib.siratemustakeem.ui

//import com.habib.siratemustakeem.databinding.ActivitySplashBinding
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.databinding.ActivityPdfviewBinding
import com.habib.siratemustakeem.databinding.ActivitySplashBinding


class PdfViewActivity : AppCompatActivity() {
    private var binding: ActivityPdfviewBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//    setContentView(R.layout.activity_splash)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pdfview)
    }
}