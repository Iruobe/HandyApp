package com.example.handyproject.data.model;

import com.google.firebase.Timestamp;

public class Review {

    private String id;
    private String handymanId;
    private String customerId;
    private String customerName;
    private int rating;
    private String text;
    private Timestamp createdAt;

    public Review() {}

    public String getId()           { return id; }
    public String getHandymanId()   { return handymanId; }
    public String getCustomerId()   { return customerId; }
    public String getCustomerName() { return customerName; }
    public int getRating()          { return rating; }
    public String getText()         { return text; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setId(String id)                     { this.id = id; }
    public void setHandymanId(String handymanId)     { this.handymanId = handymanId; }
    public void setCustomerId(String customerId)     { this.customerId = customerId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setRating(int rating)                { this.rating = rating; }
    public void setText(String text)                 { this.text = text; }
    public void setCreatedAt(Timestamp createdAt)    { this.createdAt = createdAt; }
}
