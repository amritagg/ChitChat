package com.amrit.practice.chitchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.amrit.practice.chitchat.Adapters.HomeScreenAdapter;
import com.amrit.practice.chitchat.Objects.ChatObject;
import com.amrit.practice.chitchat.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private static final String LOG_TAG = HomeActivity.class.getSimpleName();
    private ArrayList<ChatObject> chatList;
    private HomeScreenAdapter mUserAdapter;
    private ProgressBar progressBar;
    private RecyclerView mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button logOut = findViewById(R.id.log_out);
        FloatingActionButton fab = findViewById(R.id.fab);
        progressBar = findViewById(R.id.progress);

        logOut.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), FindFriendActivity.class);
            Log.e(LOG_TAG, "done");
            startActivity(intent);
        });

        initialiseRecyclerView();
        loadFriends();
    }

    private void loadFriends() {
        String curUserId = FirebaseAuth.getInstance().getUid();
        assert curUserId != null;
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference().child("user").child(curUserId).child("chat");

        mDb.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot childSnapShot : snapshot.getChildren()) { // child of chat
                        Iterable<DataSnapshot> iterator = childSnapShot.getChildren();
                        String chatId = "";
                        String publicKey = "";
                        String selfPrivateKey = "";
                        String otherPrivateKey = "";
                        String friendId = childSnapShot.getKey();

//                        if (iterator.iterator().hasNext()) {
                            chatId = Objects.requireNonNull(childSnapShot.child("chat_id").getValue()).toString();
                            publicKey = Objects.requireNonNull(childSnapShot.child("public_key").getValue()).toString();
                            selfPrivateKey = Objects.requireNonNull(childSnapShot.child("self_private_key").getValue()).toString();
                            otherPrivateKey = Objects.requireNonNull(childSnapShot.child("other_private_key").getValue()).toString();
//                        }

                        assert friendId != null;
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("user").child(friendId);
                        String finalChatId = chatId;
                        String finalPublicKey = publicKey;
                        String finalOtherPrivateKey = otherPrivateKey;
                        String finalSelfPrivateKey = selfPrivateKey;
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Log.e(LOG_TAG, "this is done " + Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                                    String friendName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                                    String photoUrl = "";
                                    String email = "";

                                    if(snapshot.child("photoUrl").exists()){
                                        photoUrl = Objects.requireNonNull(snapshot.child("photoUrl").getValue()).toString();
                                    }
                                    if(snapshot.child("email").exists()){
                                        email = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                                    }

                                    ChatObject mChat = new ChatObject(finalChatId, friendId, friendName, photoUrl, email, finalPublicKey, finalOtherPrivateKey, finalSelfPrivateKey);
//                                    Log.e(LOG_TAG, finalChatId + " " + friendId + " " + friendName + " " + finalPublicKey + " " + finalPrivateKey);
                                    chatList.add(mChat);
                                    mUserAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }

                        });

                    }

                    mUser.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }

        });

    }

    private void initialiseRecyclerView(){
        chatList = new ArrayList<>();
        mUser = findViewById(R.id.home_list);
        mUser.setVisibility(View.GONE);
        mUser.setNestedScrollingEnabled(false);
        mUser.setHasFixedSize(false);
        RecyclerView.LayoutManager mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mUser.setLayoutManager(mChatLayoutManager);
        mUserAdapter = new HomeScreenAdapter(chatList, this);
        mUser.setAdapter(mUserAdapter);
    }

}