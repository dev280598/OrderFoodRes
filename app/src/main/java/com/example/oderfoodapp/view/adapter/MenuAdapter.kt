package com.example.oderfoodapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.oderfoodapp.databinding.MenuItemBinding
import com.example.oderfoodapp.model.Category
import com.example.oderfoodapp.view.view_holder.MenuViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class MenuAdapter(
    options: FirebaseRecyclerOptions<Category>,
    private val onClickItem: (Int) -> Unit
) :
    FirebaseRecyclerAdapter<Category, MenuViewHolder>(
        options
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder.create(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int, model: Category) {
        holder.bind(model)
        holder.binding.root.setOnClickListener {
            onClickItem.invoke(position)
        }
    }
}