package com.example.oderfoodapp.view.view_holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.oderfoodapp.`interface`.ItemClickListener
import com.example.oderfoodapp.R

class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    var txtFoodName: TextView
    var txtFoodPrice: TextView
    var FoodImageView: ImageView
    var fav_img: ImageView
    var img_cart: ImageView
    private var itemClickListener: ItemClickListener? = null

    init {
        txtFoodName = itemView.findViewById(R.id.food_name)
        txtFoodPrice = itemView.findViewById(R.id.food_price)
        FoodImageView = itemView.findViewById(R.id.food_im)
        fav_img = itemView.findViewById(R.id.img_fav)
        img_cart = itemView.findViewById(R.id.img_cart)
        itemView.setOnClickListener(this)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    override fun onClick(view: View) {
        itemClickListener!!.onClick(view, adapterPosition, false)
    }
}