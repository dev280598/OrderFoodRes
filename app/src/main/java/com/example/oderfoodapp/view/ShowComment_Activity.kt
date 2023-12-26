package com.example.oderfoodapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.oderfoodapp.R
import com.example.oderfoodapp.utils.Common
import com.example.oderfoodapp.databinding.ActivityShowCommentBinding
import com.example.oderfoodapp.model.Rating
import com.example.oderfoodapp.view.view_holder.ShowCommentViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ShowComment_Activity : AppCompatActivity() {
    private var layoutManager: RecyclerView.LayoutManager? = null
    var database: FirebaseDatabase? = null
    var ratingTbl: DatabaseReference? = null
    var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    var adapter: FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>? = null
    var foodId: String? = ""
    private lateinit var binding: ActivityShowCommentBinding
    override fun onStop() {
        super.onStop()
        if (adapter != null) adapter!!.stopListening()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_comment_)
        database = FirebaseDatabase.getInstance()
        ratingTbl = database!!.getReference("Rating")
        layoutManager = LinearLayoutManager(this)
        binding.recyclerComment.layoutManager = layoutManager
        mSwipeRefreshLayout = findViewById<View>(R.id.swipe_layout) as SwipeRefreshLayout
        mSwipeRefreshLayout!!.setOnRefreshListener {
            if (intent != null) foodId = intent.getStringExtra(Common.INTENT_FOOD_ID)
            if (foodId!!.isNotEmpty() && foodId != null) {
                val query = ratingTbl!!.orderByChild("foodId").equalTo(foodId)
                val options = FirebaseRecyclerOptions.Builder<Rating>()
                    .setQuery(query, Rating::class.java)
                    .build()
                adapter = object : FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                    override fun onBindViewHolder(
                        holder: ShowCommentViewHolder,
                        position: Int,
                        model: Rating
                    ) {
                        holder.ratingBar.rating = model.rateValue!!.toFloat()
                        holder.txtComment.text = model.comment
                        holder.txtUserPhone.text = model.userPhone
                    }

                    override fun onCreateViewHolder(
                        parent: ViewGroup,
                        viewType: Int
                    ): ShowCommentViewHolder {
                        val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.show_comment_layout, parent, false)
                        return ShowCommentViewHolder(view)
                    }
                }
                loadComment(foodId!!)
            }
        }
        mSwipeRefreshLayout!!.post {
            mSwipeRefreshLayout!!.isRefreshing = true
            if (intent != null) foodId = intent.getStringExtra(Common.INTENT_FOOD_ID)
            if (!foodId!!.isEmpty() && foodId != null) {
                val query = ratingTbl!!.orderByChild("foodId").equalTo(foodId)
                val options = FirebaseRecyclerOptions.Builder<Rating>()
                    .setQuery(query, Rating::class.java)
                    .build()
                adapter = object : FirebaseRecyclerAdapter<Rating, ShowCommentViewHolder>(options) {
                    override fun onBindViewHolder(
                        holder: ShowCommentViewHolder,
                        position: Int,
                        model: Rating
                    ) {
                        holder.ratingBar.rating = model.rateValue!!.toFloat()
                        holder.txtComment.text = model.comment
                        holder.txtUserPhone.text = model.userPhone
                    }

                    override fun onCreateViewHolder(
                        parent: ViewGroup,
                        viewType: Int
                    ): ShowCommentViewHolder {
                        val view = LayoutInflater.from(parent.context)
                            .inflate(R.layout.show_comment_layout, parent, false)
                        return ShowCommentViewHolder(view)
                    }
                }
                loadComment(foodId!!)
            }
        }
    }

    private fun loadComment(foodId: String) {
        adapter!!.startListening()
        binding.recyclerComment.adapter = adapter
        mSwipeRefreshLayout!!.isRefreshing = false
    }
}