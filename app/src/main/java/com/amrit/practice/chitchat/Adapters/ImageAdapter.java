package com.amrit.practice.chitchat.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amrit.practice.chitchat.Listeners.OnImageClickListener;
import com.amrit.practice.chitchat.R;
import com.amrit.practice.chitchat.ViewModels.ImageGridRecyclerViewHolder;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageGridRecyclerViewHolder> {

    private final Context context;
    private final ArrayList<String> imageUris;
    private final OnImageClickListener onImageClickListener;

    public ImageAdapter(Context context, ArrayList<String> imageUris, OnImageClickListener onImageClickListener) {
        this.context = context;
        this.imageUris = imageUris;
        this.onImageClickListener = onImageClickListener;
    }

    @NonNull
    @Override
    public ImageGridRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_picker_grid_card, parent, false);
        return new ImageGridRecyclerViewHolder(layoutView, onImageClickListener);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ImageGridRecyclerViewHolder holder, int position) {

        holder.media.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context)
                .load(Uri.parse(imageUris.get(position)))
                .placeholder(context.getDrawable(R.drawable.ic_baseline_image_24))
                .into(holder.media);

    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

}
