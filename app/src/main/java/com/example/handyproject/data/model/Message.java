package com.example.handyproject.data.model;

import com.google.firebase.Timestamp;

public class Message {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String body;
    private boolean read;
    private Timestamp sentAt;
    private String type;
    private Timestamp bookingScheduledAt;
    private String bookingAddress;
    private String bookingNotes;
    private String bookingStatus;
    private Double bookingQuoteAmount;

    public Message() {}

    public String getMessageId()              { return messageId; }
    public String getSenderId()               { return senderId; }
    public String getReceiverId()             { return receiverId; }
    public String getBody()                   { return body; }
    public boolean isRead()                   { return read; }
    public Timestamp getSentAt()              { return sentAt; }
    public String getType()                   { return type; }
    public Timestamp getBookingScheduledAt()  { return bookingScheduledAt; }
    public String getBookingAddress()         { return bookingAddress; }
    public String getBookingNotes()           { return bookingNotes; }
    public String getBookingStatus()          { return bookingStatus; }
    public Double getBookingQuoteAmount()     { return bookingQuoteAmount; }

    public void setMessageId(String messageId)                      { this.messageId = messageId; }
    public void setSenderId(String senderId)                        { this.senderId = senderId; }
    public void setReceiverId(String receiverId)                    { this.receiverId = receiverId; }
    public void setBody(String body)                                { this.body = body; }
    public void setRead(boolean read)                               { this.read = read; }
    public void setSentAt(Timestamp sentAt)                         { this.sentAt = sentAt; }
    public void setType(String type)                                { this.type = type; }
    public void setBookingScheduledAt(Timestamp bookingScheduledAt) { this.bookingScheduledAt = bookingScheduledAt; }
    public void setBookingAddress(String bookingAddress)            { this.bookingAddress = bookingAddress; }
    public void setBookingNotes(String bookingNotes)                { this.bookingNotes = bookingNotes; }
    public void setBookingStatus(String bookingStatus)              { this.bookingStatus = bookingStatus; }
    public void setBookingQuoteAmount(Double bookingQuoteAmount)    { this.bookingQuoteAmount = bookingQuoteAmount; }
}
