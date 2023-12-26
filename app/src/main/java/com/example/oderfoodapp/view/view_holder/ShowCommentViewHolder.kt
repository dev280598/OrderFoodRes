package com.example.oderfoodapp.view.view_holder

import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.oderfoodapp.R

class ShowCommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var txtUserPhone: TextView
    var txtComment: TextView
    var ratingBar: RatingBar

    init {
        txtComment = itemView.findViewById<View>(R.id.txtComment) as TextView
        txtUserPhone = itemView.findViewById<View>(R.id.txtUserPhone) as TextView
        ratingBar = itemView.findViewById<View>(R.id.ratingBar) as RatingBar
    }
}