package com.example.oderfoodapp.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oderfoodapp.Interface.ItemClickListener;
import com.example.oderfoodapp.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtId,txtStatus,txtPhone,txtAddress;
    private ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        txtId = itemView.findViewById(R.id.order_id);
        txtStatus = itemView.findViewById(R.id.order_status);
        txtPhone = itemView.findViewById(R.id.order_phone);
        txtAddress   = itemView.findViewById(R.id.order_address);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        //itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
