package com.example.oderfoodapp.view.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.oderfoodapp.databinding.LayoutPopularCategoriesItemBinding
import com.example.oderfoodapp.model.Category

class MenuPopularViewHolder(val binding: LayoutPopularCategoriesItemBinding) :
    RecyclerView.ViewHolder(binding.root) {


    fun bind(model: Category) {
        binding.txtCategoryName.text = model.name
        Glide.with(binding.root.context).load(model.image)
            .into(binding.categoryImage)
    }

    companion object {
        fun create(binding: LayoutPopularCategoriesItemBinding): MenuPopularViewHolder {
            return MenuPopularViewHolder(binding)
        }
    }
}