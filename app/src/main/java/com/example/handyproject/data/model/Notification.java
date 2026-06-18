package com.example.handyproject.data.model;

import com.google.firebase.Timestamp;

public class Notification {
    private String id;
    private String userId;
    private String type;
    private String title;
    private String body;
    private boolean read;
    private Timestamp timestamp;

    public Notification() {}

    public String getId()           { return id; }
    public String getUserId()       { return userId; }
    public String getType()         { return type; }
    public String getTitle()        { return title; }
    public String getBody()         { return body; }
    public boolean isRead()         { return read; }
    public Timestamp getTimestamp() { return timestamp; }

    public void setId(String id)                 { this.id = id; }
    public void setUserId(String userId)         { this.userId = userId; }
    public void setType(String type)             { this.type = type; }
    public void setTitle(String title)           { this.title = title; }
    public void setBody(String body)             { this.body = body; }
    public void setRead(boolean read)             { this.read = read; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
