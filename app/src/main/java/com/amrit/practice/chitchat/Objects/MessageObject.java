package com.amrit.practice.chitchat.Objects;

public class MessageObject {

    private final String messageId;
    private final String text;
    private final String senderId;
    private final String mediaUrl;
    private final String time;

    public MessageObject(String messageId, String text, String senderId, String mediaUrl, String time) {
        this.messageId = messageId;
        this.text = text;
        this.senderId = senderId;
        this.mediaUrl = mediaUrl;
        this.time = time;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getText() {
        return text;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getTime() {
        return time;
    }

}
