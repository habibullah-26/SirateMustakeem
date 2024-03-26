package com.habib.siratemustakeem.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.databinding.ActivityDetailBinding
import com.habib.siratemustakeem.databinding.ActivityRabnaDetailBinding
import com.habib.siratemustakeem.models.Duwa

class RabnaDetail : AppCompatActivity() {
    var binding: ActivityRabnaDetailBinding? = null
    var duwaItem: Duwa? = null
    var title:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rabna_detail)
        duwaItem = intent.extras!!.getSerializable("data") as Duwa?
        title = intent.extras!!.getString("title")!!
        binding?.toplayout?.tvTitle?.text = title
        if (duwaItem != null) {
            binding?.setDuwaItem(duwaItem)
            binding?.executePendingBindings()
        }

        binding?.toplayout?.backImage?.setOnClickListener{
            finish()
        }
    }
}