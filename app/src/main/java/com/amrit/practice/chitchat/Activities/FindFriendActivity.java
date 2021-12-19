package com.amrit.practice.chitchat.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amrit.practice.chitchat.Encrypt_decrypt;
import com.amrit.practice.chitchat.Objects.FriendObject;
import com.amrit.practice.chitchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Objects;

public class FindFriendActivity extends AppCompatActivity {

    private static final String LOG_TAG = FindFriendActivity.class.getSimpleName();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        String curUserId = FirebaseAuth.getInstance().getUid();
        EditText mMailText = findViewById(R.id.edit_email_box);
        ImageButton search = findViewById(R.id.search_id);
        TextView found = findViewById(R.id.found_name);

        DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference().child("user");
        final FriendObject[] searchFriendObject = {null};

        search.setOnClickListener(view -> {
            String mail = mMailText.getText().toString();
            Query query = mUserDb.orderByChild("email").equalTo(mail);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        boolean found = false;
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            if (found) break;
                            if (snap.child("userName").getValue() != null) {
                                String name = Objects.requireNonNull(snap.child("name").getValue()).toString();
                                String userName = Objects.requireNonNull(snap.child("userName").getValue()).toString();
                                String mail = Objects.requireNonNull(snap.child("email").getValue()).toString();
                                String photoUrl = "";
                                if(snap.child("photoUrl").exists()){
                                    photoUrl = Objects.requireNonNull(snap.child("photoUrl").getValue()).toString();
                                }
                                searchFriendObject[0] = new FriendObject(name, userName, mail, snap.getKey(), photoUrl);
                                TextView textView = findViewById(R.id.found_name);
                                textView.setText(searchFriendObject[0].getUserName());
                                found = true;
                            }
                        }
                    }else{
                        found.setText("User Not Available");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }

            });

            found.setText("Searching...");

        });

        found.setOnClickListener(v -> {

            String friendId = searchFriendObject[0].getUserId();

            assert curUserId != null;
            DatabaseReference mDb = mUserDb.child(curUserId).child("chat").child(friendId);

            mDb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        createFriend(mUserDb, curUserId, friendId);
                    } else Log.e(LOG_TAG, "Already exist");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }

            });

            Log.e("find", "done till now");
            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("user").child(curUserId).child("chat").child(friendId);
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Iterable<DataSnapshot> iterator = snapshot.getChildren();
                        while (iterator.iterator().hasNext()) {
                            String chatId = iterator.iterator().next().getKey();
                            String chatName = searchFriendObject[0].getUserName();
                            Log.e("find", "done till now");
                            Log.e(LOG_TAG, chatId);
                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            intent.putExtra("chatId", chatId);
                            intent.putExtra("chatName", chatName);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

        });

    }

    private void createFriend(@NonNull DatabaseReference mUserDb, String curUserId, String friendId) {
        Log.e(LOG_TAG, "Doesn't exist");
        String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();
        assert key != null;

        KeyPair curKey = Encrypt_decrypt.getKeyPair();
        String curPrivateKey = Encrypt_decrypt.getPrivateKey(curKey);
        String curPublicKey = Encrypt_decrypt.getPublicKey(curKey);

        KeyPair friKey = Encrypt_decrypt.getKeyPair();
        String friPrivateKey = Encrypt_decrypt.getPrivateKey(friKey);
        String friPublicKey = Encrypt_decrypt.getPublicKey(friKey);

        HashMap<String, Object> curMap = new HashMap<>();
        curMap.put("public_key", curPublicKey);
        curMap.put("other_private_key", friPrivateKey);
        curMap.put("self_private_key", curPrivateKey);
        curMap.put("chat_id", key);

        HashMap<String, Object> friMap = new HashMap<>();
        friMap.put("public_key", friPublicKey);
        friMap.put("other_private_key", curPrivateKey);
        friMap.put("self_private_key", friPrivateKey);
        friMap.put("chat_id", key);

        mUserDb.child(curUserId).child("chat").child(friendId).updateChildren(curMap);
        mUserDb.child(friendId).child("chat").child(curUserId).updateChildren(friMap);
//        mUserDb.child(curUserId).child("chat").child(friendId).child(key).setValue(true);
//        mUserDb.child(friendId).child("chat").child(curUserId).child(key).setValue(true);
    }

}