package com.example.handyproject.data.model;

import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Handyman {
    private String uid;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String location;
    private String serviceCategory;
    private String serviceDescription;
    private int yearsOfExperience;
    private double hourlyRate;
    private double rating;
    private int reviewCount;
    private int totalJobs;
    private int profileViews;
    private boolean availableForHire;
    private List<String> portfolioPhotos;
    private List<String> servicesOffered;
    private String bio;
    private String responseTime;
    private Timestamp createdAt;

    public Handyman() {
        portfolioPhotos = new ArrayList<>();
        servicesOffered = new ArrayList<>();
    }

    public String getUid()                      { return uid; }
    public String getFullName()                 { return fullName; }
    public String getEmail()                    { return email; }
    public String getPhoneNumber()              { return phoneNumber; }
    public String getLocation()                 { return location; }
    public String getServiceCategory()          { return serviceCategory; }
    public String getServiceDescription()       { return serviceDescription; }
    public int getYearsOfExperience()           { return yearsOfExperience; }
    public double getHourlyRate()               { return hourlyRate; }
    public double getRating()                   { return rating; }
    public int getReviewCount()                 { return reviewCount; }
    public int getTotalJobs()                   { return totalJobs; }
    public int getProfileViews()                { return profileViews; }
    public boolean isAvailableForHire()         { return availableForHire; }
    public List<String> getPortfolioPhotos()    { return portfolioPhotos; }
    public List<String> getServicesOffered()    { return servicesOffered; }
    public String getBio()                       { return bio; }
    public String getResponseTime()              { return responseTime; }
    public Timestamp getCreatedAt()             { return createdAt; }

    public void setUid(String uid)                              { this.uid = uid; }
    public void setFullName(String fullName)                    { this.fullName = fullName; }
    public void setEmail(String email)                          { this.email = email; }
    public void setPhoneNumber(String phoneNumber)              { this.phoneNumber = phoneNumber; }
    public void setLocation(String location)                    { this.location = location; }
    public void setServiceCategory(String serviceCategory)      { this.serviceCategory = serviceCategory; }
    public void setServiceDescription(String serviceDescription){ this.serviceDescription = serviceDescription; }
    public void setYearsOfExperience(int yearsOfExperience)    { this.yearsOfExperience = yearsOfExperience; }
    public void setHourlyRate(double hourlyRate)                { this.hourlyRate = hourlyRate; }
    public void setRating(double rating)                        { this.rating = rating; }
    public void setReviewCount(int reviewCount)                 { this.reviewCount = reviewCount; }
    public void setTotalJobs(int totalJobs)                     { this.totalJobs = totalJobs; }
    public void setProfileViews(int profileViews)               { this.profileViews = profileViews; }
    public void setAvailableForHire(boolean availableForHire)   { this.availableForHire = availableForHire; }
    public void setPortfolioPhotos(List<String> portfolioPhotos){ this.portfolioPhotos = portfolioPhotos; }
    public void setServicesOffered(List<String> servicesOffered){ this.servicesOffered = servicesOffered; }
    public void setBio(String bio)                               { this.bio = bio; }
    public void setResponseTime(String responseTime)             { this.responseTime = responseTime; }
    public void setCreatedAt(Timestamp createdAt)               { this.createdAt = createdAt; }
}
