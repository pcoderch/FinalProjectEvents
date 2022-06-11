package com.pp2ex.finalprojectevents.DataStructures;

public class Message {
    private int id;
    private String content;
    private int senderId;
    private int receiverId;
    private String timestamp;

    public Message(int id, String content, int senderId, int receiverId, String timestamp) {
        this.id = id;
        this.content = content;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
    }

    // Getters
    public int getId() {return id;}
    public String getContent() { return content; }
    public int getSenderId() { return senderId; }
    public int getReceiverId() { return receiverId; }
    public String getTimestamp() { return timestamp; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

}
