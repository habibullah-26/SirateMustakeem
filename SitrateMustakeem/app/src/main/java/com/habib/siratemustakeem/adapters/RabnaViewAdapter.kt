package com.habib.siratemustakeem.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.models.Duwa
import java.util.*

class RabnaViewAdapter internal constructor(context: Context?, data: ArrayList<Duwa>) :
    RecyclerView.Adapter<RabnaViewAdapter.ViewHolder>() {
    private var mData: ArrayList<Duwa>
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null
    fun updateAdapter(duwaArrayList: ArrayList<Duwa>) {
        mData = duwaArrayList
        notifyDataSetChanged()
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_dua_rabbana, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val duwa = mData[position]
        holder.tvEnglish.text = duwa.titleEnglish?.trim().toString()
        holder.tvUrdu.text = duwa.titleUrdu?.trim().toString()
    }

    // total number of rows
    override fun getItemCount(): Int {
        return mData.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var tvEnglish: TextView
        var tvUrdu: TextView
        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }

        init {
            tvEnglish = itemView.findViewById(R.id.tvEnglish)
            tvUrdu = itemView.findViewById(R.id.tvUrdu)
            itemView.setOnClickListener(this)
        }
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): Duwa {
        return mData[id]
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    // data is passed into the constructor
    init {
        mInflater = LayoutInflater.from(context)
        mData = data
    }
}