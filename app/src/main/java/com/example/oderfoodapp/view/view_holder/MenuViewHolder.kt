package com.example.oderfoodapp.view.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.oderfoodapp.databinding.MenuItemBinding
import com.example.oderfoodapp.model.Category

class MenuViewHolder(val binding: MenuItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Category) {
        binding.menuName.text = model.name
        Glide.with(binding.root.context).load(model.image)
            .into(binding.menuIm)
    }

    companion object {
        fun create(binding: MenuItemBinding): MenuViewHolder {
            return MenuViewHolder(binding)
        }
    }
}