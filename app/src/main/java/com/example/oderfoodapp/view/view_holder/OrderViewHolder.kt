package com.example.oderfoodapp.view.view_holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.oderfoodapp.`interface`.ItemClickListener
import com.example.oderfoodapp.R

class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    var txtId: TextView
    var txtStatus: TextView
    var txtPhone: TextView
    var txtAddress: TextView
    private var itemClickListener: ItemClickListener? = null

    init {
        txtId = itemView.findViewById(R.id.order_id)
        txtStatus = itemView.findViewById(R.id.order_status)
        txtPhone = itemView.findViewById(R.id.order_phone)
        txtAddress = itemView.findViewById(R.id.order_address)
        itemView.setOnClickListener(this)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    override fun onClick(view: View) {
        //itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}