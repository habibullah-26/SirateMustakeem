package com.habib.siratemustakeem.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.adapters.MyRecyclerViewAdapter
import com.habib.siratemustakeem.databinding.ActivityMainNewBinding
import com.habib.siratemustakeem.models.Duwa
import com.habib.siratemustakeem.utils.Util
import java.util.*

class KalmaActivity : AppCompatActivity(), MyRecyclerViewAdapter.ItemClickListener {

    var TAG = "KalmaActivity"
    var binding: ActivityMainNewBinding? = null
    var duwasList: ArrayList<Duwa> = ArrayList<Duwa>()
    var mAdapter: MyRecyclerViewAdapter? = null
    var title:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_new)
        title = intent.extras!!.getString("title")!!
        binding?.toplayout?.tvTitle?.text = title
        setAdapters()
        duwasList = Util.getKalmaListFromAssets()
        mAdapter?.updateAdapter(duwasList)
        binding?.toplayout?.backImage?.setOnClickListener{
            finish()
        }
    }

    private fun setAdapters() {
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = MyRecyclerViewAdapter(this, duwasList)
        binding?.recyclerView!!.layoutManager = linearLayoutManager
        binding?.recyclerView!!.adapter = mAdapter
        mAdapter!!.setClickListener(this)
    }


    override fun onItemClick(view: View?, position: Int) {
        val duwa = mAdapter!!.getItem(position)
        val intent = Intent(this@KalmaActivity, DuwaDetail::class.java)
        intent.putExtra("data", duwa)
        intent.putExtra("title", duwa.titleUrdu)
        startActivity(intent)
    }
}