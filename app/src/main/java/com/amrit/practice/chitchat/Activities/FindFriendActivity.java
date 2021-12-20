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

import com.amrit.practice.chitchat.Utilities.Constants;
import com.amrit.practice.chitchat.Utilities.Encrypt_decrypt;
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

        DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference().child(Constants.FIRE_USER);
        final FriendObject[] searchFriendObject = {null};

        search.setOnClickListener(view -> {
            String mail = mMailText.getText().toString();
            Query query = mUserDb.orderByChild(Constants.FIRE_EMAIL).equalTo(mail);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        boolean found = false;
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            if (found) break;
                            if (snap.child(Constants.FIRE_USERNAME).getValue() != null) {
                                String name = Objects.requireNonNull(snap.child(Constants.FIRE_NAME).getValue()).toString();
                                String userName = Objects.requireNonNull(snap.child(Constants.FIRE_USERNAME).getValue()).toString();
                                String mail = Objects.requireNonNull(snap.child(Constants.FIRE_EMAIL).getValue()).toString();
                                String photoUrl = "";
                                if (snap.child(Constants.FIRE_PHOTO).exists()) {
                                    photoUrl = Objects.requireNonNull(snap.child(Constants.FIRE_PHOTO).getValue()).toString();
                                }
                                searchFriendObject[0] = new FriendObject(name, userName, mail, snap.getKey(), photoUrl);
                                TextView textView = findViewById(R.id.found_name);
                                textView.setText(searchFriendObject[0].getUserName());
                                found = true;
                            }
                        }
                    } else {
                        found.setText("User Not Available");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }

            });

            found.setText("Searching...");

        });

        found.setOnClickListener(v -> {

            String friendId = searchFriendObject[0].getUserId();

            assert curUserId != null;
            DatabaseReference mDb = mUserDb.child(curUserId).child(Constants.FIRE_CHAT).child(friendId);

            mDb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        createFriend(mUserDb, curUserId, friendId);
                    } else Log.e(LOG_TAG, "Already exist");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }

            });

            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(Constants.FIRE_USER).child(curUserId).child(Constants.FIRE_CHAT).child(friendId);
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String chatId = Objects.requireNonNull(snapshot.child(Constants.FIRE_CHAT_ID).getValue()).toString();
                        String chatName = searchFriendObject[0].getUserName();
                        String publicKey = Objects.requireNonNull(snapshot.child(Constants.FIRE_PUBLIC_KEY).getValue()).toString();
                        String selfPrivateKey = Objects.requireNonNull(snapshot.child(Constants.FIRE_SELF_PRIVATE_KEY).getValue()).toString();
                        String otherPrivateKey = Objects.requireNonNull(snapshot.child(Constants.FIRE_OTHER_PRIVATE_KEY).getValue()).toString();

                        Log.e(LOG_TAG, chatId);
                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        intent.putExtra(Constants.INTENT_CHAT_ID, chatId);
                        intent.putExtra(Constants.INTENT_CHAT_NAME, chatName);
                        intent.putExtra(Constants.INTENT_PUBLIC_KEY, publicKey);
                        intent.putExtra(Constants.INTENT_OTHER_PRIVATE_KEY, otherPrivateKey);
                        intent.putExtra(Constants.INTENT_SELF_PRIVATE_KEY, selfPrivateKey);

                        startActivity(intent);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        });

    }

    private void createFriend(@NonNull DatabaseReference mUserDb, String curUserId, String friendId) {
        Log.e(LOG_TAG, "Doesn't exist");
        String key = FirebaseDatabase.getInstance().getReference().child(Constants.FIRE_CHAT).push().getKey();
        assert key != null;

        KeyPair curKey = Encrypt_decrypt.getKeyPair();
        String curPrivateKey = Encrypt_decrypt.getPrivateKey(curKey);
        String curPublicKey = Encrypt_decrypt.getPublicKey(curKey);

        KeyPair friKey = Encrypt_decrypt.getKeyPair();
        String friPrivateKey = Encrypt_decrypt.getPrivateKey(friKey);
        String friPublicKey = Encrypt_decrypt.getPublicKey(friKey);

        HashMap<String, Object> curMap = new HashMap<>();
        curMap.put(Constants.FIRE_PUBLIC_KEY, curPublicKey);
        curMap.put(Constants.FIRE_OTHER_PRIVATE_KEY, friPrivateKey);
        curMap.put(Constants.FIRE_SELF_PRIVATE_KEY, curPrivateKey);
        curMap.put(Constants.FIRE_CHAT_ID, key);

        HashMap<String, Object> friMap = new HashMap<>();
        friMap.put(Constants.FIRE_PUBLIC_KEY, friPublicKey);
        friMap.put(Constants.FIRE_OTHER_PRIVATE_KEY, curPrivateKey);
        friMap.put(Constants.FIRE_SELF_PRIVATE_KEY, friPrivateKey);
        friMap.put(Constants.FIRE_CHAT_ID, key);

        mUserDb.child(curUserId).child(Constants.FIRE_CHAT).child(friendId).updateChildren(curMap);
        mUserDb.child(friendId).child(Constants.FIRE_CHAT).child(curUserId).updateChildren(friMap);
    }

}