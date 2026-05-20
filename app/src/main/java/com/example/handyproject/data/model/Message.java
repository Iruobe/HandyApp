package com.example.handyproject.data.model;

import com.google.firebase.Timestamp;

public class Message {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String body;
    private boolean read;
    private Timestamp sentAt;

    public Message() {}

    public String getMessageId()   { return messageId; }
    public String getSenderId()    { return senderId; }
    public String getReceiverId()  { return receiverId; }
    public String getBody()        { return body; }
    public boolean isRead()        { return read; }
    public Timestamp getSentAt()   { return sentAt; }

    public void setMessageId(String messageId)   { this.messageId = messageId; }
    public void setSenderId(String senderId)     { this.senderId = senderId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public void setBody(String body)             { this.body = body; }
    public void setRead(boolean read)            { this.read = read; }
    public void setSentAt(Timestamp sentAt)      { this.sentAt = sentAt; }
}
