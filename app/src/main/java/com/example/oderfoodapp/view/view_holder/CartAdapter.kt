package com.example.oderfoodapp.view.view_holder

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.oderfoodapp.view.CartActivity
import com.example.oderfoodapp.utils.Common
import com.example.oderfoodapp.`interface`.ItemClickListener
import com.example.oderfoodapp.R
import com.example.oderfoodapp.model.Order
import java.text.NumberFormat
import java.util.Locale

class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener,
    View.OnCreateContextMenuListener {
    var txt_cart_name: TextView
    var txt_price: TextView
    var cart_image: ImageView
    private val itemClickListener: ItemClickListener? = null
//    fun setTxt_cart_name(txt_cart_name: TextView) {
//        this.txt_cart_name = txt_cart_name
//    }

    init {
        txt_cart_name = itemView.findViewById<View>(R.id.cart_item_name) as TextView
        txt_price = itemView.findViewById<View>(R.id.cart_item_price) as TextView
        cart_image = itemView.findViewById<View>(R.id.cart_image) as ImageView
        itemView.setOnCreateContextMenuListener(this)
    }

    override fun onClick(v: View) {}
    override fun onCreateContextMenu(contextMenu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        contextMenu.setHeaderTitle("Select action")
        contextMenu.add(0, 0, getAdapterPosition(), Common.DELETE)
    }
}

class CartAdapter(listData: List<Order>, cart: CartActivity) :
    RecyclerView.Adapter<CartViewHolder?>() {
    private var listData: List<Order> = ArrayList<Order>()
    private val cart: CartActivity

    init {
        this.listData = listData
        this.cart = cart
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(cart)
        val itemView: View = inflater.inflate(R.layout.cart_layout, parent, false)
        return CartViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        /*TextDrawable drawable = TextDrawable.builder()
                .buildRound(""+listData.get(position).getQuantity(), Color.RED);
        holder.img_cart_count.setImageDrawable(drawable);*/
        Glide.with(cart.getBaseContext())
            .load(listData[position].image)
            .override(70, 70)
            .centerCrop()
            .into(holder.cart_image)
//        holder.btn_quantity.setNumber(listData[position].quantity)
//        holder.btn_quantity.setOnValueChangeListener(object : NumberPicker.OnValueChangeListener() {
//            fun onValueChange(view: ElegantNumberButton?, oldValue: Int, newValue: Int) {
//                val order: Order = listData[position]
//                order.quantity = newValue.toString()
//                Database(cart).updateCart(order)
//                var total = 0
//                val orders: List<Order> = Database(cart).carts
//                for (item in orders) total += item.price.toInt() * item.quantity.toInt()
//                val locale = Locale("en", "US")
//                val fmt = NumberFormat.getCurrencyInstance(locale)
//                cart.txtTotalPrice.setText(fmt.format(total.toLong()))
//            }
//        })
        val locale = Locale("en", "US")
        val fmt = NumberFormat.getCurrencyInstance(locale)
        val price: Int = (listData[position].price?.toInt() ?: 0) * (listData[position].quantity?.toInt() ?: 0)
        holder.txt_price.text = fmt.format(price.toLong())
        holder.txt_cart_name.setText(listData[position].productName)
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}