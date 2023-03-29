package com.habib.siratemustakeem.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.databinding.ActivityContactUsBinding

class ContactUsActivity : AppCompatActivity() {
    var binding: ActivityContactUsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_us)
        binding?.toplayout?.tvTitle?.text = "Contact Us"
        binding?.toplayout?.backImage?.setOnClickListener {
            finish()
        }
    }
}