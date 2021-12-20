package com.amrit.practice.chitchat.ViewModels;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amrit.practice.chitchat.R;

public class MessageListRecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView messageView;
    public ImageView imageView;
    public TextView messageTime;
    public LinearLayout layout;

    public MessageListRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        messageView = itemView.findViewById(R.id.message_view);
        imageView = itemView.findViewById(R.id.image_load);
        messageTime = itemView.findViewById(R.id.message_time);
        layout = itemView.findViewById(R.id.msg_item_layout);
    }
}

