package com.example.oderfoodapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.Interface.ItemClickListener;
import com.example.oderfoodapp.R;

public class MenuPopularViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txt_category_name;
    public ImageView popular_imageView;

    private ItemClickListener itemClickListener;

    public MenuPopularViewHolder(@NonNull View itemView) {

        super(itemView);
        txt_category_name = itemView.findViewById(R.id.txt_category_name);
        popular_imageView = itemView.findViewById(R.id.category_image);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false   );

    }
}
