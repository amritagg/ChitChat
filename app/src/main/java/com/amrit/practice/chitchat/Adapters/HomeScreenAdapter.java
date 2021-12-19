package com.amrit.practice.chitchat.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amrit.practice.chitchat.Activities.ChatActivity;
import com.amrit.practice.chitchat.Objects.ChatObject;
import com.amrit.practice.chitchat.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class HomeScreenAdapter extends RecyclerView.Adapter<HomeScreenAdapter.HomeChatListRecyclerViewHolder> {

    private final ArrayList<ChatObject> chatList;
    private final Context context;

    public HomeScreenAdapter(ArrayList<ChatObject> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeChatListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_card, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        return new HomeChatListRecyclerViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeChatListRecyclerViewHolder holder, int position) {
        holder.friend_id.setText(chatList.get(position).getEmail());
        holder.friend_name.setText(chatList.get(position).getFriendName());
        if(!chatList.get(position).getPhotoUrl().isEmpty()){
            Glide.with(context).load(chatList.get(position).getPhotoUrl()).into(holder.profileImage);
        }

        holder.layout.setOnClickListener(view -> {
            ChatObject chatObject = chatList.get(holder.getAdapterPosition());
            Intent intent = new Intent(view.getContext(), ChatActivity.class);
            intent.putExtra("chatId", chatObject.getChatId());
            intent.putExtra("chatName", chatObject.getFriendName());
            intent.putExtra("publicKey", chatObject.getPublicKey());
            intent.putExtra("otherPrivateKey", chatObject.getOtherPrivateKey());
            intent.putExtra("selfPrivateKey", chatObject.getSelfPrivateKey());
            Log.e("HOME", chatObject.getChatId());
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class HomeChatListRecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView friend_id;
        TextView friend_name;
        LinearLayout layout;
        ImageView profileImage;

        public HomeChatListRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            friend_id = itemView.findViewById(R.id.user_id);
            friend_name = itemView.findViewById(R.id.user_name);
            layout = itemView.findViewById(R.id.home_list_card_layout);
            profileImage = itemView.findViewById(R.id.profile_image);
        }
    }
}
