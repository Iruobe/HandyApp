package com.example.handyproject.data.model;

import com.google.firebase.Timestamp;

public class Notification {
    private String notificationId;
    private String userId;
    private String title;
    private String body;
    private String type;
    private boolean read;
    private Timestamp createdAt;

    public Notification() {}

    public String getNotificationId() { return notificationId; }
    public String getUserId()         { return userId; }
    public String getTitle()          { return title; }
    public String getBody()           { return body; }
    public String getType()           { return type; }
    public boolean isRead()           { return read; }
    public Timestamp getCreatedAt()   { return createdAt; }

    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    public void setUserId(String userId)                 { this.userId = userId; }
    public void setTitle(String title)                   { this.title = title; }
    public void setBody(String body)                     { this.body = body; }
    public void setType(String type)                     { this.type = type; }
    public void setRead(boolean read)                    { this.read = read; }
    public void setCreatedAt(Timestamp createdAt)        { this.createdAt = createdAt; }
}
