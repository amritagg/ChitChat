package com.amrit.practice.chitchat.ViewModels;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amrit.practice.chitchat.Listeners.OnImageClickListener;
import com.amrit.practice.chitchat.R;

public class ImageGridRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView media;
    OnImageClickListener onImageClickListener;

    public ImageGridRecyclerViewHolder(@NonNull View itemView, OnImageClickListener onImageClickListener) {
        super(itemView);
        media = itemView.findViewById(R.id.image_picker_image_view);
        this.onImageClickListener = onImageClickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onImageClickListener.OnImageClick(getAdapterPosition());
    }
}