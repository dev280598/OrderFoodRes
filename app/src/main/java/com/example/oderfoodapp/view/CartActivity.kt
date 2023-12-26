package com.example.oderfoodapp.view

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oderfoodapp.R
import com.example.oderfoodapp.utils.Common
import com.example.oderfoodapp.data.local.Database
import com.example.oderfoodapp.databinding.ActivityCartBinding
import com.example.oderfoodapp.model.Order
import com.example.oderfoodapp.model.Request
import com.example.oderfoodapp.view.view_holder.CartAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {
    private var layoutManager: RecyclerView.LayoutManager? = null
    var database: FirebaseDatabase? = null
    var requests: DatabaseReference? = null
    var cart: MutableList<Order> = mutableListOf()
    private var adapter: CartAdapter? = null
    lateinit var binding: ActivityCartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        database = FirebaseDatabase.getInstance()
        requests = database!!.getReference("Request")
        binding.listCart.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        binding.listCart.layoutManager = layoutManager
        actionToolBar()
        binding.btnPlaceOrder.setOnClickListener {
            if (cart.size > 0) showAlertDialog() else {
                Toast.makeText(this@CartActivity, "Your cart is empty!!", Toast.LENGTH_SHORT).show()
            }
        }
        loadListCart()
    }

    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(this@CartActivity)
        alertDialog.setTitle("One more step!")
        alertDialog.setMessage("Enter your address:")
        val edtAddress = EditText(this@CartActivity)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        edtAddress.layoutParams = lp
        alertDialog.setView(edtAddress)
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp)
        alertDialog.setPositiveButton(
            "YES"
        ) { _, _ ->
            val request = Request(
                Common.currentUser!!.phone,
                Common.currentUser!!.name,
                edtAddress.text.toString(),
                binding.total.text.toString(), cart
            )
            requests?.child(System.currentTimeMillis().toString())?.setValue(request)
            Database(this@CartActivity).cleanCart()
            Toast.makeText(this@CartActivity, "Thank you, Order Place!", Toast.LENGTH_SHORT)
                .show()
            finish()
        }
        alertDialog.show()
    }

    private fun loadListCart() {
        cart = Database(this).carts.toMutableList()
        adapter = CartAdapter(cart, this)
        adapter?.notifyDataSetChanged()
        binding.listCart.adapter = adapter
        var total = 0
        for (order in cart) total += (order.price?.toInt() ?: 0) * (order.quantity?.toInt() ?: 0)
        val locale = Locale("en", "US")
        val fmt = NumberFormat.getCurrencyInstance(locale)
        binding.total.text = fmt.format(total.toLong())
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.title == Common.DELETE) {
            deleteCart(item.order)
        }
        return true
    }

    private fun deleteCart(order: Int) {
        cart.removeAt(order)
        Database(this).cleanCart()
        for (item in cart) Database(this).addToCart(item)
        loadListCart()
    }

    private fun actionToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}