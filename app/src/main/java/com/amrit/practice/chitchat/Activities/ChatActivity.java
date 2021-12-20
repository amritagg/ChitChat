package com.amrit.practice.chitchat.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amrit.practice.chitchat.Adapters.MessageAdapter;
import com.amrit.practice.chitchat.Utilities.Constants;
import com.amrit.practice.chitchat.Utilities.Encrypt_decrypt;
import com.amrit.practice.chitchat.Objects.MessageObject;
import com.amrit.practice.chitchat.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private static final String LOG_TAG = ChatActivity.class.getSimpleName();
    private MessageAdapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private ArrayList<MessageObject> messageList;

    String chatId;
    String chatName;
    DatabaseReference mDb;
    EditText messageBox;
    String publicKey;
    String otherPrivateKey;
    String selfPrivateKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatId = getIntent().getStringExtra(Constants.INTENT_CHAT_ID);
        chatName = getIntent().getStringExtra(Constants.INTENT_CHAT_NAME);
        publicKey = getIntent().getStringExtra(Constants.INTENT_PUBLIC_KEY);
        otherPrivateKey = getIntent().getStringExtra(Constants.INTENT_OTHER_PRIVATE_KEY);
        selfPrivateKey = getIntent().getStringExtra(Constants.INTENT_SELF_PRIVATE_KEY);

        setTitle(chatName);
        mDb = FirebaseDatabase.getInstance().getReference().child(Constants.FIRE_CHAT).child(chatId);

        initializeRecyclerView();

        FloatingActionButton sendButton = findViewById(R.id.send_message);
        messageBox = findViewById(R.id.message_edit_box);

        findViewById(R.id.send_image).setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ImagePickerActivity.class);
            selectImageActivityLauncher.launch(intent);
        });

        sendButton.setOnClickListener(view -> sendMessage());
        getMessages();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sendMessage() {
        String message = messageBox.getText().toString().trim();
        if (!message.isEmpty()) {
            String messageId = mDb.push().getKey();
            @SuppressLint("SimpleDateFormat")
            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
            assert messageId != null;
            DatabaseReference messageDb = mDb.child(messageId);
            Map<String, Object> newMessageMap = new HashMap<>();
            newMessageMap.put(Constants.FIRE_SENDER_ID, FirebaseAuth.getInstance().getUid());
            String msg = messageBox.getText().toString();
            String encryptMsg = Encrypt_decrypt.encryptRSAToString(msg, publicKey);
            newMessageMap.put(Constants.FIRE_TEXT, encryptMsg);
            newMessageMap.put(Constants.FIRE_TIME, date);
            messageDb.updateChildren(newMessageMap);
            messageBox.setText(null);
            mChatAdapter.notifyDataSetChanged();
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private void sendImage(String uriString, @NonNull String mediaMessage) {

        Toast.makeText(this, "Your Image is being send...", Toast.LENGTH_LONG).show();
        String messageId = mDb.push().getKey();
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
        assert messageId != null;
        final DatabaseReference newMessageDb = mDb.child(messageId);
        final Map<String, Object> newMessageMap = new HashMap<>();
        newMessageMap.put(Constants.FIRE_SENDER_ID, FirebaseAuth.getInstance().getUid());
        newMessageMap.put(Constants.FIRE_TIME, date);

        if(!mediaMessage.isEmpty()){
            String encryptMsg = Encrypt_decrypt.encryptRSAToString(mediaMessage, publicKey);
            newMessageMap.put(Constants.FIRE_TEXT, encryptMsg);
        }

        String mediaId = mDb.child(Constants.FIRE_IMAGE).push().getKey();
        assert mediaId != null;
        StorageReference filePath = FirebaseStorage.getInstance().getReference().child("chat").child(chatId).child(messageId);
        UploadTask uploadTask = filePath.putFile(Uri.parse(uriString));
        uploadTask.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
            newMessageMap.put("/image/" + mediaId + "/", uri.toString());
            newMessageDb.updateChildren(newMessageMap);
            mChatAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Image sent successfully!!!", Toast.LENGTH_SHORT).show();
        }));

    }

    private void getMessages() {
        mDb.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    String text = "";
                    String senderId = "";
                    String mediaUrl = "";
                    String time = "";

                    if (snapshot.child(Constants.FIRE_SENDER_ID).getValue() != null) {
                        senderId = Objects.requireNonNull(snapshot.child(Constants.FIRE_SENDER_ID).getValue()).toString();
                    }
                    if (snapshot.child(Constants.FIRE_IMAGE).getValue() != null) {
                        for (DataSnapshot mediaSnapShot : snapshot.child(Constants.FIRE_IMAGE).getChildren()) {
                            mediaUrl = Objects.requireNonNull(mediaSnapShot.getValue()).toString();
                        }
                    }
                    if(snapshot.child(Constants.FIRE_TIME).getValue() != null){
                        time = Objects.requireNonNull(snapshot.child(Constants.FIRE_TIME).getValue()).toString();
                    }
                    if (snapshot.child(Constants.FIRE_TEXT).getValue() != null) {
                        String msg = Objects.requireNonNull(snapshot.child(Constants.FIRE_TEXT).getValue()).toString();
                        if(senderId.equals(FirebaseAuth.getInstance().getUid())){
                            text = Encrypt_decrypt.decryptRSAToString(msg, selfPrivateKey);
                        }else{
                            text = Encrypt_decrypt.decryptRSAToString(msg, otherPrivateKey);
                        }
                    }

                    MessageObject messageObject = new MessageObject(snapshot.getKey(), text, senderId, mediaUrl, time);
                    messageList.add(messageObject);
                    mChatAdapter.notifyDataSetChanged();
                    mChatLayoutManager.scrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }

        });
    }

    private void initializeRecyclerView() {
        messageList = new ArrayList<>();
        RecyclerView mChat = findViewById(R.id.chat_recycler_view);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(messageList, this, FirebaseAuth.getInstance().getUid());
        mChat.setAdapter(mChatAdapter);
    }

    ActivityResultLauncher<Intent> selectImageActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    Bundle bundle = data.getExtras();
                    String uriString = bundle.getString(Constants.INTENT_MEDIA_URI);
                    String mediaMessage = bundle.getString(Constants.INTENT_MEDIA_MESSAGE);

                    sendImage(uriString, mediaMessage);
                }
            }
    );

}