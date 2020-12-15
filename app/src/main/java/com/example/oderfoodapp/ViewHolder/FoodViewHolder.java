package com.example.oderfoodapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.Interface.ItemClickListener;
import com.example.oderfoodapp.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtFoodName,txtFoodPrice;
    public ImageView FoodImageView,fav_img,img_cart;
    private ItemClickListener itemClickListener;

    public FoodViewHolder(@NonNull View itemView) {

        super(itemView);
        txtFoodName = itemView.findViewById(R.id.food_name);
        txtFoodPrice = itemView.findViewById(R.id.food_price);
        FoodImageView = itemView.findViewById(R.id.food_im);
        fav_img = itemView.findViewById(R.id.img_fav);
        img_cart = itemView.findViewById(R.id.img_cart);
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
