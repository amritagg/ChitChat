package com.amrit.practice.chitchat.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amrit.practice.chitchat.Activities.ShowImageActivity;
import com.amrit.practice.chitchat.Objects.MessageObject;
import com.amrit.practice.chitchat.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageListRecyclerViewHolder> {

    private final ArrayList<MessageObject> messageList;
    private final Context context;
    private final String uid;

    public MessageAdapter(ArrayList<MessageObject> messageList, Context context, String uid) {
        this.messageList = messageList;
        this.context = context;
        this.uid = uid;
    }

    @NonNull
    @Override
    public MessageListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams")
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_card, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        return new MessageListRecyclerViewHolder(layoutView);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MessageListRecyclerViewHolder holder, int position) {

        String senderId = messageList.get(position).getSenderId();
        holder.messageTime.setText(messageList.get(position).getTime());
        if(senderId.equals(uid)){
            holder.layout.setBackground(context.getDrawable(R.drawable.message_send_back));
            holder.messageView.setTextColor(context.getColor(android.R.color.white));
        }else{
            holder.layout.setBackground(context.getDrawable(R.drawable.messaeg_receive_back));
            holder.messageView.setTextColor(context.getColor(android.R.color.black));
        }

        String message = messageList.get(position).getText();
        if(message.isEmpty()){
            holder.messageView.setVisibility(View.GONE);
        }else {
            holder.messageView.setVisibility(View.VISIBLE);
            holder.messageView.setText(message);
        }

        String imageUrl = messageList.get(position).getMediaUrl();

        if(imageUrl.isEmpty()) holder.imageView.setVisibility(View.GONE);
        else {
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(imageUrl).into(holder.imageView);

            holder.imageView.setOnClickListener(view -> {
                Intent intent = new Intent(context.getApplicationContext(), ShowImageActivity.class);
                intent.putExtra(ShowImageActivity.INTENT_FLAG, 0);
                intent.putExtra(ShowImageActivity.INTENT_URI, imageUrl);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageListRecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView messageView;
        ImageView imageView;
        TextView messageTime;
        LinearLayout layout;

        public MessageListRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            messageView = itemView.findViewById(R.id.message_view);
            imageView = itemView.findViewById(R.id.image_load);
            messageTime = itemView.findViewById(R.id.message_time);
            layout = itemView.findViewById(R.id.msg_item_layout);
        }
    }

}
