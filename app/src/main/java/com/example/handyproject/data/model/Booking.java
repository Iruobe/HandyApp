package com.example.handyproject.data.model;

import com.google.firebase.Timestamp;

public class Booking {
    private String bookingId;
    private String customerId;
    private String handymanId;
    private String customerName;
    private String handymanName;
    private String serviceCategory;
    private String status;
    private String notes;
    private String address;
    private double totalCost;
    private Double quoteAmount; // boxed — null until the handyman confirms with a quote
    private Timestamp scheduledAt;
    private Timestamp createdAt;

    public Booking() {}

    public String getBookingId()       { return bookingId; }
    public String getCustomerId()      { return customerId; }
    public String getHandymanId()      { return handymanId; }
    public String getCustomerName()    { return customerName; }
    public String getHandymanName()    { return handymanName; }
    public String getServiceCategory() { return serviceCategory; }
    public String getStatus()          { return status; }
    public String getNotes()           { return notes; }
    public String getAddress()         { return address; }
    public double getTotalCost()       { return totalCost; }
    public Double getQuoteAmount()     { return quoteAmount; }
    public Timestamp getScheduledAt()  { return scheduledAt; }
    public Timestamp getCreatedAt()    { return createdAt; }

    public void setBookingId(String bookingId)             { this.bookingId = bookingId; }
    public void setCustomerId(String customerId)           { this.customerId = customerId; }
    public void setHandymanId(String handymanId)           { this.handymanId = handymanId; }
    public void setCustomerName(String customerName)       { this.customerName = customerName; }
    public void setHandymanName(String handymanName)       { this.handymanName = handymanName; }
    public void setServiceCategory(String serviceCategory) { this.serviceCategory = serviceCategory; }
    public void setStatus(String status)                   { this.status = status; }
    public void setNotes(String notes)                     { this.notes = notes; }
    public void setAddress(String address)                 { this.address = address; }
    public void setTotalCost(double totalCost)             { this.totalCost = totalCost; }
    public void setQuoteAmount(Double quoteAmount)         { this.quoteAmount = quoteAmount; }
    public void setScheduledAt(Timestamp scheduledAt)      { this.scheduledAt = scheduledAt; }
    public void setCreatedAt(Timestamp createdAt)          { this.createdAt = createdAt; }
}
