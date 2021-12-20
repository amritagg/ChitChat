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
import com.amrit.practice.chitchat.Utilities.Constants;
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
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference().child(Constants.FIRE_USER).child(curUserId).child(Constants.FIRE_CHAT);

        mDb.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapShot : snapshot.getChildren()) { // child of chat
                        String friendId = childSnapShot.getKey();

                        String chatId = Objects.requireNonNull(childSnapShot.child(Constants.FIRE_CHAT_ID).getValue()).toString();
                        String publicKey = Objects.requireNonNull(childSnapShot.child(Constants.FIRE_PUBLIC_KEY).getValue()).toString();
                        String selfPrivateKey = Objects.requireNonNull(childSnapShot.child(Constants.FIRE_SELF_PRIVATE_KEY).getValue()).toString();
                        String otherPrivateKey = Objects.requireNonNull(childSnapShot.child(Constants.FIRE_OTHER_PRIVATE_KEY).getValue()).toString();

                        assert friendId != null;
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(Constants.FIRE_USER).child(friendId);
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Log.e(LOG_TAG, "this is done " + Objects.requireNonNull(snapshot.child(Constants.FIRE_NAME).getValue()).toString());
                                    String friendName = Objects.requireNonNull(snapshot.child(Constants.FIRE_NAME).getValue()).toString();
                                    String photoUrl = "";
                                    String email = "";

                                    if (snapshot.child(Constants.FIRE_PHOTO).exists()) {
                                        photoUrl = Objects.requireNonNull(snapshot.child(Constants.FIRE_PHOTO).getValue()).toString();
                                    }
                                    if (snapshot.child(Constants.FIRE_EMAIL).exists()) {
                                        email = Objects.requireNonNull(snapshot.child(Constants.FIRE_EMAIL).getValue()).toString();
                                    }

                                    ChatObject mChat = new ChatObject(chatId, friendId, friendName, photoUrl, email, publicKey, otherPrivateKey, selfPrivateKey);
                                    chatList.add(mChat);
                                    mUserAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }

                        });

                    }

                    mUser.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

    }

    private void initialiseRecyclerView() {
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