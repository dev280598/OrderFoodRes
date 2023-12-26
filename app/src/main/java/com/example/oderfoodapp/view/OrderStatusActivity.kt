package com.example.oderfoodapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oderfoodapp.R
import com.example.oderfoodapp.utils.Common
import com.example.oderfoodapp.databinding.ActivityOrderStatusBinding
import com.example.oderfoodapp.model.Request
import com.example.oderfoodapp.view.view_holder.OrderViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class OrderStatusActivity : AppCompatActivity() {
    private var layoutManager: RecyclerView.LayoutManager? = null
    var adapter: FirebaseRecyclerAdapter<Request, OrderViewHolder>? = null
    var database: FirebaseDatabase? = null
    private var requests: DatabaseReference? = null
    private lateinit var binding: ActivityOrderStatusBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_status)
        database = FirebaseDatabase.getInstance()
        requests = database!!.getReference("Request")
        binding.listOrders.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        binding.listOrders.layoutManager = layoutManager
        actionToolBar()
        loadOrders(Common.currentUser!!.phone)
    }

    private fun loadOrders(phone: String?) {
        val getOrderByUser = requests!!.orderByChild("phone").equalTo(phone)
        val orderOptions = FirebaseRecyclerOptions.Builder<Request>()
            .setQuery(getOrderByUser, Request::class.java).build()
        adapter = object : FirebaseRecyclerAdapter<Request, OrderViewHolder>(orderOptions) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.order_layout, parent, false)
                return OrderViewHolder(itemView)
            }

            override fun onBindViewHolder(
                ViewHolder: OrderViewHolder,
                position: Int,
                model: Request
            ) {
                ViewHolder.txtId.text = adapter!!.getRef(position).key
                ViewHolder.txtStatus.text = convertCodeToStatus(model.status)
                ViewHolder.txtAddress.text = model.address
                ViewHolder.txtPhone.text = model.phone
            }
        }
        adapter?.startListening()
        adapter?.notifyDataSetChanged()
        binding.listOrders.adapter = adapter
    }

    private fun convertCodeToStatus(status: String?): String {
        return if (status == "0") "Placed" else if (status == "1") "On my way" else "Shipped"
    }

    private fun actionToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}