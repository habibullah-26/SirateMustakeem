package com.habib.siratemustakeem.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.databinding.ActivityContactUsBinding

class ContactUsActivity : AppCompatActivity() {
    var binding: ActivityContactUsBinding? = null

    private val supportEmail = "engineer.habib26@gmail.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_us)

        binding?.toplayout?.tvTitle?.text = getString(R.string.contact_us_title_urdu)
        binding?.toplayout?.backImage?.setOnClickListener {
            finish()
        }

        binding?.btnEmailUs?.setOnClickListener { openEmailCompose() }
        binding?.tvEmail?.setOnClickListener { openEmailCompose() }
    }

    private fun openEmailCompose() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}
