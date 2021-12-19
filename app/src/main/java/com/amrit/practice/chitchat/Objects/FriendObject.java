package com.amrit.practice.chitchat.Objects;

public class FriendObject {
    private final String name;
    private final String userName;
    private final String mail;
    private final String userId;
    private final String photoUrl;

    public FriendObject(String name, String userName, String mail, String userId, String photoUrl) {
        this.name = name;
        this.userName = userName;
        this.mail = mail;
        this.userId = userId;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
