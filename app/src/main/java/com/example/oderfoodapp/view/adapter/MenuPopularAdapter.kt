package com.example.oderfoodapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.oderfoodapp.databinding.LayoutPopularCategoriesItemBinding
import com.example.oderfoodapp.model.Category
import com.example.oderfoodapp.view.view_holder.MenuPopularViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class MenuPopularAdapter(options: FirebaseRecyclerOptions<Category>) :
    FirebaseRecyclerAdapter<Category, MenuPopularViewHolder>(options) {
    override fun onBindViewHolder(
        viewHolder: MenuPopularViewHolder,
        position: Int,
        model: Category
    ) {
        viewHolder.bind(model)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuPopularViewHolder {
        val binding = LayoutPopularCategoriesItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuPopularViewHolder.create(binding)
    }
}