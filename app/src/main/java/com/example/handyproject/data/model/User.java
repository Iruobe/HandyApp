package com.example.handyproject.data.model;

import com.google.firebase.Timestamp;

public class User {
    private String uid;
    private String role;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String location;
    private Timestamp createdAt;
    private String serviceCategory;
    private String serviceDescription;
    private double hourlyRate;
    private String bio;
    private String responseTime;

    public User() {}

    public User(String uid, String role, String fullName, String email,
                String phoneNumber, String location, Timestamp createdAt) {
        this.uid = uid;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.createdAt = createdAt;
    }

    public String getUid()         { return uid; }
    public String getRole()        { return role; }
    public String getFullName()    { return fullName; }
    public String getEmail()       { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getLocation()    { return location; }
    public Timestamp getCreatedAt(){ return createdAt; }
    public String getServiceCategory()    { return serviceCategory; }
    public String getServiceDescription() { return serviceDescription; }
    public double getHourlyRate()         { return hourlyRate; }
    public String getBio()                { return bio; }
    public String getResponseTime()       { return responseTime; }

    public void setUid(String uid)                 { this.uid = uid; }
    public void setRole(String role)               { this.role = role; }
    public void setFullName(String fullName)       { this.fullName = fullName; }
    public void setEmail(String email)             { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setLocation(String location)       { this.location = location; }
    public void setCreatedAt(Timestamp createdAt)  { this.createdAt = createdAt; }
    public void setServiceCategory(String serviceCategory)       { this.serviceCategory = serviceCategory; }
    public void setServiceDescription(String serviceDescription) { this.serviceDescription = serviceDescription; }
    public void setHourlyRate(double hourlyRate)                 { this.hourlyRate = hourlyRate; }
    public void setBio(String bio)                               { this.bio = bio; }
    public void setResponseTime(String responseTime)             { this.responseTime = responseTime; }
}
