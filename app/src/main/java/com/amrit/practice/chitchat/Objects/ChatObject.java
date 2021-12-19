package com.amrit.practice.chitchat.Objects;

public class ChatObject {

    private final String chatId;
    private final String friendId;
    private final String friendName;
    private final String photoUrl;
    private final String email;
    private final String publicKey;
    private final String otherPrivateKey;
    private final String selfPrivateKey;

    public ChatObject(String chatId, String friendId, String friendName, String photoUrl, String email, String publicKey, String otherPrivateKey, String selfPrivateKey) {
        this.chatId = chatId;
        this.friendId = friendId;
        this.friendName = friendName;
        this.photoUrl = photoUrl;
        this.email = email;
        this.publicKey = publicKey;
        this.otherPrivateKey = otherPrivateKey;
        this.selfPrivateKey = selfPrivateKey;
    }

    public String getChatId() {
        return chatId;
    }

    public String getFriendId() {
        return friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getEmail(){
        return email;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getOtherPrivateKey() {
        return otherPrivateKey;
    }

    public String getSelfPrivateKey() {
        return selfPrivateKey;
    }
}
