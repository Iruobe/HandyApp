package com.example.handyproject.data.model;

import com.google.firebase.Timestamp;

public class Notification {
    private String id;
    private String userId;
    private String type;
    private String title;
    private String body;
    private boolean read;
    private Timestamp createdAt;
    private String conversationId;
    private String relatedId;

    public Notification() {}

    public String getId()             { return id; }
    public String getUserId()         { return userId; }
    public String getType()           { return type; }
    public String getTitle()          { return title; }
    public String getBody()           { return body; }
    public boolean isRead()           { return read; }
    public Timestamp getCreatedAt()   { return createdAt; }
    public String getConversationId() { return conversationId; }
    public String getRelatedId()      { return relatedId; }

    public void setId(String id)                         { this.id = id; }
    public void setUserId(String userId)                 { this.userId = userId; }
    public void setType(String type)                     { this.type = type; }
    public void setTitle(String title)                   { this.title = title; }
    public void setBody(String body)                     { this.body = body; }
    public void setRead(boolean read)                    { this.read = read; }
    public void setCreatedAt(Timestamp createdAt)        { this.createdAt = createdAt; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    public void setRelatedId(String relatedId)           { this.relatedId = relatedId; }
}
