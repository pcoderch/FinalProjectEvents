package com.pp2ex.finalprojectevents;

public class Message {
    private String content;
    private int senderId;
    private int receiverId;

    public Message(String content, int senderId, int receiverId) {
        this.content = content;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    // Getters
    public String getContent() { return content; }
    public int getSenderId() { return senderId; }
    public int getReceiverId() { return receiverId; }

    // Setters
    public void setContent(String content) { this.content = content; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

}
