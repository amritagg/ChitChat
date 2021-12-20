package com.amrit.practice.chitchat.ViewModels;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amrit.practice.chitchat.R;

public class HomeChatListRecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView friend_id;
    public TextView friend_name;
    public LinearLayout layout;
    public ImageView profileImage;

    public HomeChatListRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        friend_id = itemView.findViewById(R.id.user_id);
        friend_name = itemView.findViewById(R.id.user_name);
        layout = itemView.findViewById(R.id.home_list_card_layout);
        profileImage = itemView.findViewById(R.id.profile_image);
    }
}

